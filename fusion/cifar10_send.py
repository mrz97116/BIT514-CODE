# -*- coding:utf-8 -*-

import os
import sys
import time
import pickle
import random
import numpy as np

batch_carves = 5    # 将图片分为5部分存储
class_num = 5
image_size = 32
img_channels = 3
class_count = 5 # 类别:目前是5类

train_num = 3000    # 训练数据大小
dev_num = 1000      # 验证数据大小
test_num = 1000     # 测试数据大小

# ========================================================== #
# ├─ prepare_data()
#  ├─ download training data if not exist by download_data()
#  ├─ load data by load_data()
#  └─ shuffe and return data
# ========================================================== #

Map = {'animals':0,'architecture':1,'art':2,'cars_motorcycles':3,'celebrities':4}   # 需要修改

def unpickle(file):
    with open(file, 'rb') as fo:
        dict = pickle.load(fo, encoding='bytes')
    return dict


def load_data_one(file):
    batch = unpickle(file)
    data = batch['data']
    labels = batch['labels']
    print("Loading %s : %d." % (file, len(data)))
    return data, labels


def load_data(files, data_dir, label_count):
    global image_size, img_channels
    data, labels = load_data_one(data_dir + '/' + files[0])
    for f in files[1:]:
        data_n, labels_n = load_data_one(data_dir + '/' + f)
        data = np.append(data, data_n, axis=0)
        labels = np.append(labels, labels_n, axis=0)
    # labels = np.array([[float(i == label) for i in range(label_count)] for label in labels])
    labels = np.array([[float(Map[label] == i) for i in range(label_count)] for label in labels])
    data = data.reshape([-1, img_channels, image_size, image_size])
    print(data.shape)
    data = data.transpose([0, 2, 3, 1])
    return data, labels


def prepare_data():

    print("======Loading data======")
    # download_data()
    # train_data_dir = './bin_test/ans_images/train'
    dev_data_dir = './bin_test/ans_images/dev'
    test_data_dir = './bin_test/ans_images/test'
    image_dim = image_size * image_size * img_channels
    # meta = unpickle(data_dir + '/batches.meta')
    #
    # label_names = meta[b'label_names']
    # label_count = len(label_names)
    # train_files = ['train_batch_%d' % d for d in range(batch_carves)]
    # train_data, train_labels = load_data(train_files, train_data_dir, class_count)
    # for i in train_labels:print(i)
    # print(type(train_data))
    # dev_files = ['dev_batch_%d' % d for d in range(batch_carves)]
    # dev_data, dev_labels = load_data(dev_files, dev_data_dir, class_count)

    test_files = ['test_batch_%d' % d for d in range(batch_carves)]
    test_data, test_labels = load_data(test_files, test_data_dir, class_count)

    # print("Train data:", np.shape(train_data), np.shape(train_labels))
    # print("Dev data :", np.shape(dev_data), np.shape(dev_labels))
    print("Test data :", np.shape(test_data), np.shape(test_labels))
    # for i in test_data:
    #     print(i)
    #     print(type(i))
    print("======Load finished======")
    print("======Shuffling data======")
    # indices = np.random.permutation(len(train_data))
    # train_data = train_data[indices]
    # train_labels = train_labels[indices]
    #
    # indices1 = np.random.permutation(len(dev_data))
    # dev_data = dev_data[indices1]
    # dev_labels = dev_labels[indices1]

    indices2 = np.random.permutation(len(test_data))
    test_data = test_data[indices2]
    test_labels = test_labels[indices2]
    print("======Prepare Finished======")

    return test_data, test_labels, indices2

# prepare_data()
# ========================================================== #
# ├─ _random_crop()
# ├─ _random_flip_leftright()
# ├─ data_augmentation()
# └─ color_preprocessing()
# ========================================================== #

def _random_crop(batch, crop_shape, padding=None):
    oshape = np.shape(batch[0])

    if padding:
        oshape = (oshape[0] + 2 * padding, oshape[1] + 2 * padding)
    new_batch = []
    npad = ((padding, padding), (padding, padding), (0, 0))
    for i in range(len(batch)):
        new_batch.append(batch[i])
        if padding:
            new_batch[i] = np.lib.pad(batch[i], pad_width=npad,
                                      mode='constant', constant_values=0)
        nh = random.randint(0, oshape[0] - crop_shape[0])
        nw = random.randint(0, oshape[1] - crop_shape[1])
        new_batch[i] = new_batch[i][nh:nh + crop_shape[0],
                       nw:nw + crop_shape[1]]
    return new_batch


def _random_flip_leftright(batch):
    for i in range(len(batch)):
        if bool(random.getrandbits(1)):
            batch[i] = np.fliplr(batch[i])
    return batch


def color_preprocessing(x_test):

    x_test = x_test.astype('float32')

    x_test[:, :, :, 0] = (x_test[:, :, :, 0] - np.mean(x_test[:, :, :, 0])) / np.std(x_test[:, :, :, 0])
    x_test[:, :, :, 1] = (x_test[:, :, :, 1] - np.mean(x_test[:, :, :, 1])) / np.std(x_test[:, :, :, 1])
    x_test[:, :, :, 2] = (x_test[:, :, :, 2] - np.mean(x_test[:, :, :, 2])) / np.std(x_test[:, :, :, 2])
    # x_test[:, :, :, 3] = (x_test[:, :, :, 3] - np.mean(x_test[:, :, :, 3])) / np.std(x_test[:, :, :, 3])

    return x_test

def data_augmentation(batch):
    batch = _random_flip_leftright(batch)
    batch = _random_crop(batch, [32, 32], 4)
    return batch