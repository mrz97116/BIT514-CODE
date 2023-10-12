#!/usr/bin/env python
# -*- coding: UTF-8 -*-
# File: pickled.py
# Author: Yahui Liu <yahui.cvrs@gmail.com>

import os
import pickle

BIN_COUNTS = 5

def pickled(savepath, data, label, fnames, bin_num=BIN_COUNTS,mode="train",name='hello'):
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
    start = i*samples_per_bin
    end = (i+1)*samples_per_bin
    
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

    with open(os.path.join(savepath, name+str(idx)), 'wb') as fi:
      pickle.dump(dict, fi)
    idx = idx + 1

def unpickled(filename):
  #assert os.path.isdir(filename)
  assert os.path.isfile(filename)
  with open(filename, 'rb') as fo:
    dict = pickle.load(fo)
  return dict
