import tensorflow as tf
from tflearn.layers.conv import global_avg_pool
from tensorflow.contrib.layers import batch_norm, flatten
from tensorflow.contrib.framework import arg_scope
from cifar10_send import *
import numpy as np
import pickle

# init_learning_rate = 0.01  # 0.01可以,0.1太大,0.001太小
cardinality = 8 # how many split ?
blocks = 3 # res_block ! (split + transition)
depth = 64 # out channel

"""
So, the total number of layers is (3*blokcs)*residual_layer_num + 2
because, blocks = split(conv 2) + transition(conv 1) = 3 layer
and, first conv layer 1, last dense layer 1
thus, total number of layers = (3*blocks)*residual_layer_num + 2
"""
reduction_ratio = 4

# batch_num = 3000
# batch_size = 16 # 1024*3的情况下，最大值为95
# iteration = int((batch_num+batch_size-1)/batch_size)
# # 128 * 391 ~ 50,000

dev_batch = 1000    # 预测集图片数量
dev_add = 16        # 每批测试的图片数量
dev_iteration = int((dev_batch+dev_add-1)/dev_add)  # 向上取整,将预测集图片分的批数 = 1000/16

test_batch = 1000
test_add = 16
test_iteration = int((test_batch+test_add-1)/test_add)

total_epochs = 2

text_feat_len = 300
test_x, test_y, indices2 = prepare_data()
test_x = color_preprocessing(test_x)

text_dev_feat = 'bin_test/ans_texts/dev_feat.pkl'
text_test_feat = 'bin_test/ans_texts/test_feat.pkl'

weight_image = 0.7  # default:0.7
weight_text = 1 - weight_image  # default:0.3


weight_decay = 0.0005
momentum = 0.9
init_learning_rate = 0.01

def conv_layer(input, filter, kernel, stride, padding='SAME', layer_name="conv"):
    with tf.name_scope(layer_name):
        network = tf.layers.conv2d(inputs=input, use_bias=False, filters=filter, kernel_size=kernel, strides=stride, padding=padding)
        return network

def Global_Average_Pooling(x):
    return global_avg_pool(x, name='Global_avg_pooling')

def Average_pooling(x, pool_size=[2,2], stride=2, padding='SAME'):
    return tf.layers.average_pooling2d(inputs=x, pool_size=pool_size, strides=stride, padding=padding)

def Batch_Normalization(x, training, scope):
    with arg_scope([batch_norm],
                   scope=scope,
                   updates_collections=None,
                   decay=0.9,
                   center=True,
                   scale=True,
                   zero_debias_moving_mean=True) :
        return tf.cond(training,
                       lambda : batch_norm(inputs=x, is_training=training, reuse=None),
                       lambda : batch_norm(inputs=x, is_training=training, reuse=True))

def Relu(x):
    return tf.nn.relu(x)

def Sigmoid(x) :
    return tf.nn.sigmoid(x)

def Concatenation(layers) :
    return tf.concat(layers, axis=3)

def Fully_connected(x, units=class_num, layer_name='fully_connected') :
    with tf.name_scope(layer_name) :
        return tf.layers.dense(inputs=x, use_bias=True, units=units)


def get_test_feat():
    f = open(text_test_feat, 'rb')
    a = pickle.load(f)
    a = a[indices2]
    f.close()
    return a  # np.array类型

def Test(sess, epoch_learning_rate):
    test_acc = 0.0
    test_loss = 0.0
    test_pre_index = 0
    predict = []
    test_feat = get_test_feat()

    for it in range(test_iteration):

        if test_pre_index + test_add < test_batch:
            test_batch_x = test_x[test_pre_index: test_pre_index + test_add]
            test_batch_y = test_y[test_pre_index: test_pre_index + test_add]
            iter_feat = test_feat[test_pre_index:test_pre_index + test_add]
        else:
            test_batch_x = test_x[test_pre_index:]
            test_batch_y = test_y[test_pre_index:]
            iter_feat = test_feat[test_pre_index:]

        # test_batch_x = data_augmentation(test_batch_x)
        test_feed_dict = {
            x: test_batch_x,
            text_feat: iter_feat,
            label: test_batch_y,
            learning_rate: epoch_learning_rate,
            training_flag: False
        }

        pred, loss_, acc_ = sess.run([logits, cost, accuracy], feed_dict=test_feed_dict)

        predict.extend(pred)
        test_loss += loss_
        test_acc += acc_
        test_pre_index = test_pre_index + test_add

    test_loss /= test_iteration # average loss
    test_acc /= test_iteration # average accuracy

    summary = tf.Summary(value=[tf.Summary.Value(tag='test_loss', simple_value=test_loss),
                                tf.Summary.Value(tag='test_accuracy', simple_value=test_acc)])

    return test_acc, test_loss, summary


class SE_ResNeXt():
    def __init__(self, x, text_feat, training):
        self.training = training
        self.model = self.Build_SEnet(x,text_feat)

    def first_layer(self, x, scope):
        with tf.name_scope(scope) :
            x = conv_layer(x, filter=64, kernel=[3, 3], stride=1, layer_name=scope+'_conv1')
            x = Batch_Normalization(x, training=self.training, scope=scope+'_batch1')
            x = Relu(x)

            return x

    def transform_layer(self, x, stride, scope):
        with tf.name_scope(scope) :
            x = conv_layer(x, filter=depth, kernel=[1,1], stride=1, layer_name=scope+'_conv1')
            x = Batch_Normalization(x, training=self.training, scope=scope+'_batch1')
            x = Relu(x)

            x = conv_layer(x, filter=depth, kernel=[3,3], stride=stride, layer_name=scope+'_conv2')
            x = Batch_Normalization(x, training=self.training, scope=scope+'_batch2')
            x = Relu(x)
            return x

    def transition_layer(self, x, out_dim, scope):
        with tf.name_scope(scope):
            x = conv_layer(x, filter=out_dim, kernel=[1,1], stride=1, layer_name=scope+'_conv1')
            x = Batch_Normalization(x, training=self.training, scope=scope+'_batch1')
            # x = Relu(x)
            return x

    def split_layer(self, input_x, stride, layer_name):
        with tf.name_scope(layer_name) :
            layers_split = list()
            for i in range(cardinality) :
                splits = self.transform_layer(input_x, stride=stride, scope=layer_name + '_splitN_' + str(i))
                layers_split.append(splits)

            return Concatenation(layers_split)

    def squeeze_excitation_layer(self, input_x, out_dim, ratio, layer_name):
        with tf.name_scope(layer_name) :

            squeeze = Global_Average_Pooling(input_x)

            excitation = Fully_connected(squeeze, units=out_dim / ratio, layer_name=layer_name+'_fully_connected1')
            excitation = Relu(excitation)
            excitation = Fully_connected(excitation, units=out_dim, layer_name=layer_name+'_fully_connected2')
            excitation = Sigmoid(excitation)

            excitation = tf.reshape(excitation, [-1,1,1,out_dim])
            scale = input_x * excitation

            return scale

    def residual_layer(self, input_x, out_dim, layer_num, res_block=blocks):
        # split + transform(bottleneck) + transition + merge
        # input_dim = input_x.get_shape().as_list()[-1]

        for i in range(res_block):
            input_dim = int(np.shape(input_x)[-1])

            if input_dim * 2 == out_dim:
                flag = True
                stride = 2
                channel = input_dim // 2
            else:
                flag = False
                stride = 1

            x = self.split_layer(input_x, stride=stride, layer_name='split_layer_'+layer_num+'_'+str(i))
            x = self.transition_layer(x, out_dim=out_dim, scope='trans_layer_'+layer_num+'_'+str(i))
            x = self.squeeze_excitation_layer(x, out_dim=out_dim, ratio=reduction_ratio, layer_name='squeeze_layer_'+layer_num+'_'+str(i))

            if flag is True :
                pad_input_x = Average_pooling(input_x)
                pad_input_x = tf.pad(pad_input_x, [[0, 0], [0, 0], [0, 0], [channel, channel]]) # [?, height, width, channel]
            else :
                pad_input_x = input_x

            input_x = Relu(x + pad_input_x)

        return input_x

    def Build_SEnet(self, input_x, text_feat):
        # only cifar10 architecture

        input_x = self.first_layer(input_x, scope='first_layer')
        x = self.residual_layer(input_x, out_dim=64, layer_num='1')
        x = self.residual_layer(x, out_dim=128, layer_num='2')
        x = self.residual_layer(x, out_dim=256, layer_num='3')

        x = Global_Average_Pooling(x)
        x = flatten(x)
        ########################################################################
        # todo,模型融合后期融合，特征融合，更新feat
        # 获取文本模型特征
        print(type(x),type(text_feat))
        print(x.shape,text_feat.shape)
        # text_feat = Fully_connected(text_feat,units=256)
        #x = process(x)
        #text_feat = process(text_feat)
        # x_mix = tf.concat([x,text_feat],1)

        ####################################
        # # todo:融合一:0.7 resnext图片pre+0.3 cnn文本pre -> 全连接
        # x = Fully_connected(x, units=class_num, layer_name='hello')
        # x_mix = tf.concat([0.7*x, 0.3*text_feat], 1)
        # x_mix = Fully_connected(x_mix, units=class_num, layer_name='world')
        # # x_mix = Fully_connected(x_mix, units=class_num, layer_name='world2')
        # return x_mix,x_mix
        ####################################
        #
        ####################################
        # todo:融合二:0.7 图片pre+0.3 word2vec 文本pre
        # pre_image = Fully_connected(x,units=class_num)
        # pre_text = Fully_connected(text_feat,units=class_num)
        # x = pre_image+pre_text
        # return x, x
        ####################################
        # todo:融合三:0.7图片pre+0.3 word2vec 文本pre -> 全连接
        ####################################
        pre_image = Fully_connected(x, units=class_num)
        pre_text = Fully_connected(text_feat, units=class_num)
        x = tf.concat([weight_image*pre_image, weight_text*pre_text], 1)
        x = Fully_connected(x, units=class_num)
        return x, x
        ########################################################################

# image_size = 32, img_channels = 3, class_num = 10 in cifar10
x = tf.placeholder(tf.float32, shape=[None, image_size, image_size, img_channels])
text_feat = tf.placeholder(tf.float32, shape=[None,text_feat_len])
label = tf.placeholder(tf.float32, shape=[None, class_num])

training_flag = tf.placeholder(tf.bool)

learning_rate = tf.placeholder(tf.float32, name='learning_rate')

# feature = SE_ResNeXt(x, training = training_flag).feature
feature,logits = SE_ResNeXt(x, text_feat = text_feat, training = training_flag).model
cost = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(labels=label, logits=logits))

l2_loss = tf.add_n([tf.nn.l2_loss(var) for var in tf.trainable_variables()])
optimizer = tf.train.MomentumOptimizer(learning_rate=learning_rate, momentum=momentum, use_nesterov=True)
train = optimizer.minimize(cost + l2_loss * weight_decay)

correct_prediction = tf.equal(tf.argmax(logits, 1), tf.argmax(label, 1))
accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))

saver = tf.train.Saver(tf.global_variables())


with tf.Session() as sess:
    epoch_learning_rate = init_learning_rate
    saver.restore(sess, './0.7res+0.3word+con2/later_best.ckpt')    # 读取模型
    test_acc, test_loss, test_summary = Test(sess, epoch_learning_rate)
    print(test_acc) # 测试准确率
