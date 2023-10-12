
import numpy as np
from glob import glob
import os, cv2
from pickled import *
from os import listdir

# initial
DATA_LEN = 3072
CHANNEL_LEN = 1024
SHAPE = 32
Picture_sum = 16500

def imread(im_path, shape = SHAPE, color="RGB", mode=cv2.IMREAD_UNCHANGED):

    # print('path',im_path) # 打印图片路径
    im = np.float32(cv2.imread(im_path,1))
    im = np.array(im)
    # print("图片长、宽、通道数：",im.shape)  # 打印图片形状

    if(im.shape == ()) :
        print("非法图片：",im_path)
        return False,None   # 这里可以鉴定非法图片

    if color == "RGB":
        # print(cv2.COLOR_BAYER_BG2BGR)
        im = cv2.cvtColor(im, cv2.COLOR_BGR2RGB)
    # im = np.transpose(im, [2, 1, 0])
    if shape != None:
    # assert isinstance(shape, int)
        im = cv2.resize(im, (shape, shape))
    return True,im

def read_data(path, shape=None, color='RGB'):
    """
       filename (str): a file
         data file is stored in such format:
           image_name  label
       data_path (str): image data folder
       return (numpy): a array of image and a array of label
    """
    errorCount = 0
    data = np.zeros((Picture_sum,DATA_LEN))
    labels = []
    image_list = []

    classes = listdir(path)
    idx = 0
    s, c = SHAPE, CHANNEL_LEN
    for i in classes:
        i_path = os.path.join(path,i)
        for image_name in listdir(i_path):
            # print(image_name)
            labels.append(int(i))   # 这里的标签转为整数比较好处理，因为类别和数组下标是一致的
            image_list.append(image_name)
            # print(glob(i_path+'/'+image_name)[0])
            flag,im = imread(glob(i_path+'/'+image_name)[0], shape=s, color='RGB')
            if(flag):
                data[idx, :c] = np.reshape(im[:, :, 0], c)
                data[idx, c:2 * c] = np.reshape(im[:, :, 1], c)
                data[idx, 2 * c:] = np.reshape(im[:, :, 2], c)
                idx = idx + 1
            else:
                errorCount += 1
    print(labels)
    print(image_list)
    print("非法图片个数errorCountS: ",errorCount)
    return data, labels, image_list


BIN_COUNTS = 5


def pickled(savepath, data, label, fnames, bin_num=BIN_COUNTS, mode="train"):
    '''
      savepath (str): save path
      data (array): image data, a nx3072 array
      label (list): image label, a list with length n
      fnames (str list): image names, a list with length n
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

        with open(os.path.join(savepath, 'data_batch_' + str(idx)), 'wb') as fi:
            pickle.dump(dict, fi)
        idx = idx + 1

if __name__ == '__main__':
    path = 'Images/'
    data, label, lst = read_data(path, shape=32)
    save_path = './bin'
    pickled(save_path, data, label, lst, bin_num = 1)