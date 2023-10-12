import os
from os import listdir
from glob import glob
import random
import pickle
import numpy as np
import cv2


DATA_LEN = 3072
CHANNEL_LEN = 1024
SHAPE = 32
image_path = 'E:/MyWork/PycharmProjects/interest_prediction/media'

def get_sum(image_data):
    '''
    获取图片数目
    :param image_data:
    :return:
    '''
    sum = 0
    for i in image_data:
        sum = sum+len(i[1])
    print('numbers of images:',sum)
    return sum

def imread(im_path, shape = 32, color="RGB", mode=cv2.IMREAD_UNCHANGED):

    # print('path',im_path) # 打印图片路径
    im = np.float32(cv2.imread(im_path,1))
    im = np.array(im)
    # print(im_path)
    # print("图片长、宽、通道数：",im.shape)  # 打印图片形状

    # if(im.shape == ()) :
    #     print("非法图片：",im_path)
    #     os.remove(im_path)
    #     return False,None   # 这里可以鉴定非法图片

    if color == "RGB":
        # print(cv2.COLOR_BAYER_BG2BGR)
        im = cv2.cvtColor(im, cv2.COLOR_BGR2RGB)
    # im = np.transpose(im, [2, 1, 0])
    if shape != None:
    # assert isinstance(shape, int)
        im = cv2.resize(im, (shape, shape))
    return True,im

def read_data(image_data):
    """
       filename (str): a file
         data file is stored in such format:
           image_name  label
       data_path (str): small_image data folder
       return (numpy): a array of small_image and a array of label
    """
    errorCount = 0
    picture_sum = get_sum(image_data)
    data = np.zeros((picture_sum,DATA_LEN))
    labels = []
    image_list = []
    # id = []
    idx = 0
    s, c = SHAPE, CHANNEL_LEN

    for cate in image_data:
        label,images = cate[0],cate[1]
        # print(label,images)
        cate_path = os.path.join(image_path )
        print(cate_path)
        for image_name in images:
            # print(image_name)
            # print(i[0],i[1])
            labels.append(label)   # 这里的标签转为整数比较好处理，因为类别和数组下标是一致的
            image_list.append(image_name)

            # print(glob(i_path+'/'+image_name)[0])
            # print(cate_path,image_name)
            # print(cate_path,type(cate_path),image_name,type(image_name))
            cate_image_path = cate_path+'/'+image_name+'.jpg'
            # print(cate_image_path)
            flag,im = imread(cate_image_path, shape=s, color='RGB')
            data[idx, :c] = np.reshape(im[:, :, 0], c)
            data[idx, c:2 * c] = np.reshape(im[:, :, 1], c)
            data[idx, 2 * c:] = np.reshape(im[:, :, 2], c)
            idx = idx+1
        # print(labels)
        # print(image_list)
        # save_to_pkl('bin/index.pkl',id)
    return data, labels, image_list

def pickled(savepath, data, label, fnames, pickle_name, bin_num=5, mode="test"):
    '''
      savepath (str): save path
      data (array): small_image data, a nx3072 array
      label (list): small_image label, a list with length n
      fnames (str list): small_image names, a list with length n
      bin_num (int): save data in several files
      mode (str): {'train', 'test'}
    '''
    assert os.path.isdir(savepath)
    total_num = len(fnames)
    samples_per_bin = int(total_num / bin_num)
    assert samples_per_bin > 0
    idx = 0
    for i in range(bin_num):
        start = i * samples_per_bin
        end = (i + 1) * samples_per_bin
        if(i == bin_num-1): end = total_num

        if end <= total_num:
            dict = {'data': data[start:end, :],
                    'labels': label[start:end],
                    'filenames': fnames[start:end]}
        else:
            dict = {'data': data[start:, :],
                    'labels': label[start:],
                    'filenames': fnames[start:]}
        if mode == "train":
            dict['batch_label'] = "training batch {} of {}".format(idx, bin_num)
        else:
            dict['batch_label'] = "testing batch {} of {}".format(idx, bin_num)

        with open(os.path.join(savepath, pickle_name + str(idx)), 'wb') as fi:
            pickle.dump(dict, fi)
        idx = idx + 1

def carve_image(image_link, image_tmp1, image_tmp2):
    '''
    对图片进行划分
    :param image_link:
    :param image_tmp1:
    :param image_tmp2:
    :return:
    '''
    train_ = image_link[:image_tmp1]
    dev_ = image_link[image_tmp1:image_tmp2]
    test_ = image_link[image_tmp2:]

    train_ = sorted(train_)
    dev_ = sorted(dev_)
    test_ = sorted(test_)

    return train_,dev_,test_

def save_image(train_images, dev_images, test_images):
    '''
    把训练数据、预测数据、测试数据保存为cifar10格式
    :param train_images:
    :param dev_images:
    :param test_images:
    :return:
    '''
    # print('train_images,type',train_images,type(train_images),len(train_images[0][1]),len(train_images[1][1]))
    # for i in train_images:print(i[0],i[1])

    train_data,train_label,train_lst = read_data(train_images)
    dev_data, dev_label, dev_lst = read_data(dev_images)
    test_data, test_label, test_lst = read_data(test_images)

    train_save_path = 'ans_images/train'
    dev_save_path = 'ans_images/dev'
    test_save_path = 'ans_images/test'

    pickled(train_save_path,train_data, train_label, train_lst, pickle_name = 'train_batch_',bin_num=5)
    print('train_image is saved!')
    pickled(dev_save_path, dev_data, dev_label, dev_lst, pickle_name = 'dev_batch_', bin_num=5)
    print('dev_image is saved!')
    pickled(test_save_path, test_data, test_label, test_lst, pickle_name = 'test_batch_', bin_num=5)
    print('test_image is saved!')

cate = 'art'
test_ = 'E:/MyWork/PycharmProjects/interest_prediction/media'
test_image = [[cate, test_]]
test_data, test_label, test_lst = read_data(test_image)
test_save_path = 'bin_pre/ans_images/test'
pickled(test_save_path, test_data, test_label, test_lst, pickle_name = 'test_batch_', bin_num=1)
print('test_image is saved!')