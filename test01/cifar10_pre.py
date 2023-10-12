# -*- coding:utf-8 -*-

import os
import sys
import time
import pickle
import random
import numpy as np

batch_carves = 1    # 将图片分为5部分存储
image_size = 32
img_channels = 3

#===========================================================#
# todo:修改参数

# Map = {'animals':0,
#       'architecture':1,
#       'art':2,
#       'cars_motorcycles':3,
#       'celebrities':4,}

class_num = 32   # 需要修改的地方
batch_num = 100  # 265431
root_path = 'E:/MyWork/PycharmProjects/pinsRecognation/test01/bin_pre/'

# root_path = 'bin/'
# class_num = 32   # 需要修改的地方
# batch_num = 22089  # 265431
# dev_batch = 6311   # 88478
# test_batch = 3156  # 88497

# root_path = 'bin_paper/'
Map = {'animals':0,
       'architecture':1,
        'art':2,
        'cars_motorcycles':3,
        'celebrities':4,
       'design':5,
        'diy_crafts':6,
        'education':7,
        'film_music_books':8,
        'food_drink':9,
        'gardening':10,
        'geek':11,
        'hair_beauty':12,
        'health_fitness':13,
        'history':14,
        'holidays_events':15,
        'home_decor':16,
        'humor':17,
        'illustrations_posters':18,
        'kids':19,
        'mens_fashion':20,
        'outdoors':21,
        'photography':22,
        'products':23,
        'quotes':24,
        'science_nature':25,
        'sports':26,
        'tattoos':27,
        'technology':28,
        'travel':29,
        'weddings':30,
        'womens_fashion':31
        }   # 需要修改
#===========================================================#

# ========================================================== #
# ├─ prepare_data()
#  ├─ download training data if not exist by download_data()
#  ├─ load data by load_data()
#  └─ shuffe and return data
# ========================================================== #

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

    train_data_dir = root_path+'ans_image_0'
    train_files = ['train_batch_%d' % d for d in range(batch_carves)]
    train_data, train_labels = load_data(train_files, train_data_dir, class_num)
    print("======Load finished======")
    print("======Shuffling data======")
    indices = np.random.permutation(len(train_data))
    train_data = train_data[indices]
    train_labels = train_labels[indices]

    return train_data, train_labels, indices

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


def color_preprocessing(x_train):

    x_train = x_train.astype('float32')

    x_train[:, :, :, 0] = (x_train[:, :, :, 0] - np.mean(x_train[:, :, :, 0])) / np.std(x_train[:, :, :, 0])
    x_train[:, :, :, 1] = (x_train[:, :, :, 1] - np.mean(x_train[:, :, :, 1])) / np.std(x_train[:, :, :, 1])
    x_train[:, :, :, 2] = (x_train[:, :, :, 2] - np.mean(x_train[:, :, :, 2])) / np.std(x_train[:, :, :, 2])
    # x_train[:, :, :, 3] = (x_train[:, :, :, 3] - np.mean(x_train[:, :, :, 3])) / np.std(x_train[:, :, :, 3])

    return x_train

def data_augmentation(batch):
    batch = _random_flip_leftright(batch)
    batch = _random_crop(batch, [32, 32], 4)
    return batch