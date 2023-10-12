import tensorflow as tf
from tflearn.layers.conv import global_avg_pool
from tensorflow.contrib.layers import batch_norm, flatten
from tensorflow.contrib.framework import arg_scope
from cifar10_pre import *
import numpy as np
import pickle

# init_learning_rate = 0.01  # 0.01可以,0.1太大,0.001太小
cardinality = 8 # how many split ?,default=8
blocks = 3 # res_block ! (split + transition),default = 3
depth = 64 # out channel, default = 64

"""
So, the total number of layers is (3*blokcs)*residual_layer_num + 2
because, blocks = split(conv 2) + transition(conv 1) = 3 layer
and, first conv layer 1, last dense layer 1
thus, total number of layers = (3*blocks)*residual_layer_num + 2
"""
reduction_ratio = 4

batch_size = 32 # 1024*3的情况下，最大值为95
iteration = int((batch_num+batch_size-1)/batch_size)
# 128 * 391 ~ 50,000

test_add = 32
test_iteration = int((batch_num+test_add-1)/test_add)

# total_epochs = 100

accuracy_train = []
accuracy_train_image = []
accuracy_train_text = []

loss_train = []

accuracy_dev = []
accuracy_dev_image = []
accuracy_dev_text = []

loss_dev = []

text_feat_len = 300
test_x, test_y, indices = prepare_data()
test_x = color_preprocessing(test_x)

text_test_feat = root_path+'ans_text_0/pre_feat.pkl'

weight_decay = 0.0005
momentum = 0.9

ans_acc = 0.0
init_learning_rate = 0.01
weight_1 = 0.7
weight_2 = 0.3
lr_step = 5  # default:5
lr_times =  5 # default:10
best_acc = 0.0

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

def Fully_connected1(x, units=class_num, layer_name='fully_connected') :
    with tf.name_scope(layer_name) :
        # x = tf.layers.dense(inputs=x, use_bias=True, units=512, activation=tf.nn.sigmoid)
        # x = tf.layers.dense(inputs=x, use_bias=True, units=256, activation=tf.nn.sigmoid)
        return tf.layers.dense(inputs=x, use_bias=True, units=units, activation=tf.nn.sigmoid)

def Max_pooling(x, pool_size=[3,3], stride=2, padding='VALID') :
    return tf.layers.max_pooling2d(inputs=x, pool_size=pool_size, strides=stride, padding=padding)

def save_to_file(file_name,content):
    f = open(file_name,'wb')
    pickle.dump(content,f)
    f.close()

def save_to_pkl(file_name, content):
    file = open(file_name, 'wb')
    pickle.dump(content, file)
    file.close()

def get_test_feat():
    f = open(text_test_feat, 'rb')
    a = pickle.load(f)
    a = a[indices]
    f.close()
    return a  # np.array类型


def accuracy_judge(pre,y):
    '''
    根据预测值和标签值计算准确率
    :param pre: np.array (32,5)
    :param y:np.array   (32,5)
    :return:
    '''
    test_y = []
    for i in pre:
        i = list(i)
        index = i.index(max(i))
        test_y.append(index)

    label = []
    for i in y:
        i = list(i)
        index = i.index(max(i))
        label.append(index)
    # print(test_y, label)
    test_y = np.array(test_y)
    label = np.array(label)
    count = np.sum(test_y == label)
    # print(count,pre.shape[0])
    acc = count*1.0/pre.shape[0]
    # print(acc)
    return acc
    # print(pre.type)
    # print(y.type)
    # print(pre.shape,y.shape)

def Test(sess, weight_1, weight_2):
    test_acc = 0.0
    test_image_acc = 0.0
    test_text_acc = 0.0

    test_loss = 0.0
    test_pre_index = 0
    predict = []
    test_feat = get_test_feat()
    this_num = 0
    for it in range(test_iteration):

        if test_pre_index + test_add < batch_num:
            test_batch_x = test_x[test_pre_index: test_pre_index + test_add]
            test_batch_y = test_y[test_pre_index: test_pre_index + test_add]
            iter_feat = test_feat[test_pre_index:test_pre_index + test_add]
            this_num = test_add
        else:
            test_batch_x = test_x[test_pre_index:]
            test_batch_y = test_y[test_pre_index:]
            iter_feat = test_feat[test_pre_index:]
            this_num = test_y[0]-test_pre_index

        # test_batch_x = data_augmentation(test_batch_x)
        test_feed_dict = {
            x: test_batch_x,
            text_feat: iter_feat,
            label: test_batch_y,
            learning_rate: epoch_learning_rate,
            training_flag: False,
            weight_image: weight_1,
            weight_text: weight_2,
            num: this_num
        }

        pred, loss_, acc_ = sess.run([logits, cost, accuracy], feed_dict=test_feed_dict)

        predict.extend(pred.tolist())
        test_loss += loss_

        test_acc += acc_
        # test_image_acc += image_acc_
        # test_text_acc += text_acc_

        test_pre_index = test_pre_index + test_add

    test_loss /= test_iteration # average loss
    test_acc /= test_iteration # average accuracy

    ans = []
    for i in predict:
        ans.append(i.index(max(i)))
    ans = sorted(ans)
    print(ans)

    summary = tf.Summary(value=[tf.Summary.Value(tag='test_loss', simple_value=test_loss),
                                tf.Summary.Value(tag='test_accuracy', simple_value=test_acc)])

    return test_acc, test_image_acc, test_text_acc, test_loss, summary

class SE_ResNeXt():
    def __init__(self, x, text_feat, training, weight_img, weight_txt, num):
        self.training = training
        self.model = self.Build_SEnet(x, text_feat, weight_img, weight_txt, num)

    def first_layer(self, x, scope):
        with tf.name_scope(scope) :
            x = conv_layer(x, filter=64, kernel=[3, 3], stride=1, layer_name=scope+'_conv1')    # kernel = [3,3]
            x = Batch_Normalization(x, training=self.training, scope=scope+'_batch1')
            x = Relu(x)
            # x = tf.nn.max_pool(x,[1,3,3,1],[1,2,2,1],padding='VALID')
            # x = Max_pooling(x)
            return x

    def second_layer(self, x, scope):
        with tf.name_scope(scope) : # filter:卷积核的个数，有几个就输出几层
            x = conv_layer(x, filter=1, kernel=[3, 3], stride=1, layer_name=scope+'_conv1')    # kernel = [3,3]
            x = Batch_Normalization(x, training=self.training, scope=scope+'_batch1')
            x = Relu(x)

            return x

    def self_layer(self, x, scope):
        with tf.name_scope(scope) : # filter:卷积核的个数，有几个就输出几层
            x = conv_layer(x, filter=1, kernel=[3, 3], stride=1, layer_name=scope+'_conv1')    # kernel = [3,3]
            tf.nn.max_pool(x, [1, 1, 1, 1], [1, 1, 1, 1], padding='VALID')
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

    def Build_SEnet(self, input_x, text_feat, weight_img, weight_txt, num):
        '''
        3,4,6,3:50
        3,4,23,3:101
        3,,8,36,3:152
        :param input_x:
        :param text_feat:
        :param weight_img:
        :param weight_txt:
        :param num:
        :return:
        '''
        # only cifar10 architecture
        input_x = self.first_layer(input_x, scope='first_layer')
        x = self.residual_layer(input_x, out_dim=64, layer_num='1')    # 64,3
        x = self.residual_layer(x, out_dim=128, layer_num='2')  # 128,8
        x = self.residual_layer(x, out_dim=256, layer_num='3')  # 256,36
        # # x = self.residual_layer(x, out_dim=512, layer_num='4', res_block=3)# add,3
        #
        x = Global_Average_Pooling(x)
        x = flatten(x)

        ########################################################################
        ####################################
        # todo:仅有图片的分类
        # x = Fully_connected(x, units=class_num)
        # return x, x

        ####################################
        # todo：仅有文本的分类
        # pre_text = Fully_connected(text_feat, units=class_num)
        # return pre_text, pre_text

        ####################################
        # 结果融合一 0.3+0.7
        # # todo:融合一:0.3 image pre+0.7 text pre
        # pre_image = Fully_connected(x,units=class_num)
        # pre_text = Fully_connected(text_feat,units=class_num)
        # x = 0.3*pre_image+0.7*pre_text
        # return x, x

        ####################################
        # 结果融合二 0.5concat0.5
        # todo:融合二:0.5 image pre 结果维度拼接 0.5 text pre
        pre_image = Fully_connected(x, units=class_num)
        pre_text = Fully_connected(text_feat, units=class_num)
        x_mix = tf.concat([0.5 * pre_image, 0.5 * pre_text], 1)
        x_mix = Fully_connected(x_mix, units=class_num, layer_name='hello')
        return x_mix, x_mix

        ####################################
        # 特征融合一 concat
        # todo:融合三:image特征 拼接 text特征
        # x = tf.concat([x, text_feat], 1)
        # x = Fully_connected(x, units=class_num)
        # return x, x

        ####################################
        # 特征融合二 pool_concat
        # todo:融合四:image特征池化 拼接 text特征池化
        # text_feat = tf.reshape(text_feat, [-1, 30, 10, 1])
        # text_feat = tf.nn.avg_pool(text_feat, [1, 1, 1, 1], [1, 1, 1, 1], padding='VALID')
        # text_feat = tf.reshape(text_feat, [-1, 300])
        #
        # x = tf.reshape(x, [-1, 16, 16, 1])
        # x = tf.nn.avg_pool(x, [1, 1, 1, 1], [1, 1, 1, 1], padding='VALID')
        # x = tf.reshape(x, [-1, 256])
        #
        # x = tf.concat([x, text_feat], 1)
        # x = Fully_connected(x, units=class_num)
        # return x, x
        ####################################
        ########################################################################

# image_size = 32, img_channels = 3, class_num = 10 in cifar10
#xx = tf.placeholder(tf.float32, shape=[None,image_feat_len+text_feat_len])
x = tf.placeholder(tf.float32, shape=[None, image_size, image_size, img_channels])
text_feat = tf.placeholder(tf.float32, shape=[None,text_feat_len])
label = tf.placeholder(tf.float32, shape=[None, class_num])
weight_image = tf.placeholder(tf.float32)
weight_text = tf.placeholder(tf.float32)
num = tf.placeholder(tf.int32)

training_flag = tf.placeholder(tf.bool)
learning_rate = tf.placeholder(tf.float32, name='learning_rate')

# feature = SE_ResNeXt(x, training = training_flag).feature
# feature,logits, logits_image, logits_text = SE_ResNeXt(x, text_feat = text_feat, training = training_flag, weight_img=weight_image, weight_txt=weight_text).model
feature,logits = SE_ResNeXt(x, text_feat = text_feat, training = training_flag, weight_img=weight_image, weight_txt=weight_text, num = num).model
cost = tf.reduce_mean(tf.nn.softmax_cross_entropy_with_logits(labels=label, logits=logits))

l2_loss = tf.add_n([tf.nn.l2_loss(var) for var in tf.trainable_variables()])
optimizer = tf.train.MomentumOptimizer(learning_rate=learning_rate, momentum=momentum, use_nesterov=True)
train = optimizer.minimize(cost + l2_loss * weight_decay)

correct_prediction = tf.equal(tf.argmax(logits, 1), tf.argmax(label, 1))
# correct_prediction_image = tf.equal(tf.argmax(logits_image, 1), tf.argmax(label, 1))
# correct_prediction_text = tf.equal(tf.argmax(logits_text, 1), tf.argmax(label, 1))

accuracy = tf.reduce_mean(tf.cast(correct_prediction, tf.float32))
# accuracy_image = tf.reduce_mean(tf.cast(correct_prediction_image, tf.float32))
# accuracy_text = tf.reduce_mean(tf.cast(correct_prediction_text, tf.float32))

saver = tf.train.Saver(tf.global_variables())

with tf.Session() as sess:

    epoch_learning_rate = init_learning_rate
    saver.restore(sess, './later_best.ckpt')
    test_acc, test_image_acc, test_text_acc, test_loss, test_summary = Test(sess, weight_1, weight_2)
    print(test_acc)


