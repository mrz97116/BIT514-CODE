################################################
'''
功能：将清洗过的图片和文本分类并进行保存
'''
################################################
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
image_path = 'test_Images'
text_path = 'test_Texts'
text_list = ['animals.csv','architecture.csv','art.csv','cars_motorcycles.csv','celebrities.csv']

# train_image_path = 'ans_images/train'
# dev_image_path = 'ans_images/dev'
# test_image_path = 'ans_images/test'
#
# train_test_path = 'ans_text_0/train'
# dev_test_path = 'ans_text_0/dev'
# test_test_path = 'ans_text_0/test'

# # 删除csv某一列
# import csv
#
# def del_cvs_col(fname, newfname, idxs, delimiter=' '):
#     with open(fname) as csvin, open(newfname, 'w') as csvout:
#         reader = csv.reader(csvin, delimiter=delimiter)
#         writer = csv.writer(csvout, delimiter=delimiter)
#         rows = (tuple(item for idx, item in enumerate(row) if idx not in idxs) for row in reader)
#         writer.writerows(rows)
#
# del_cvs_col('a.csv', 'b.csv', [2])

# 删除csv某一行
# f = open('texts/1.csv','r+',encoding='utf-8')
# a = f.readlines()
# del a[0]
# print(a)
# f.close()
#
# f = open('texts/1.csv','w+',encoding='utf-8')
# f.writelines(a)
# f.close()

# 读取非法文件下标
# import pickle
# f = open('bin/index.pkl','rb')
# content = pickle.load(f)    # list类型
# f.close()

# 对csv进行排序
def text_sort(text_path):
    '''
    对所有文本进行排序，方便查找对应文本
    :param text_path:
    :return:
    '''
    for file in listdir(text_path):
        file_path = glob(text_path+'/'+file)[0]
        f = open(file_path,'r+',encoding='utf-8')
        content = f.readlines()
        content = sorted(content,key=lambda i: (i.strip().split(',')[3]) if len(i.strip().split(',')) > 3 else '0')
        # f.writelines(content) 没关文件就直接写会加在后面而不是更新文件
        f.close()

        f = open(file_path,'w+',encoding='utf-8')
        f.writelines(content)
        f.close()

def get_link(class_path):
    '''
    获取图片目录下图片名称列表
    :param class_path:
    :return:
    '''
    image_link = []
    for image_name in listdir(class_path):
        link = image_name.split('.')[0] # str
        image_link.append(link)
    return image_link

def get_text(cate,image_link):
    '''
    根据图片名称找到相应的文本
    :param cate:
    :param image_link:
    :return:
    '''
    i = 0
    text_content = []
    # image_link = sorted(image_link)
    image_num = len(image_link)
    # text_name = glob(text_path+'/'+cate+'.csv')[0]
    text_name = os.path.join(text_path + '/', cate+'.csv')
    # print(text_path,cate,text_name)
    f = open(text_name,'r+',encoding='utf-8')
    content = f.readlines()
    for line in content:
        words = line.strip().split(',')
        if(len(words) > 3):
            if(words[3] == image_link[i]):
                # print('第%d个图片--%s--对应的文本:\n' % (i, image_link[i]),line)
                text_content.append(words[4]+'\n')
                i = i + 1
                if(i == image_num):return text_content
    f.close()

def save_to_file(file_name,content):
    '''
    将文本保存到相应文件ans_text中
    :param file_name:
    :param content:
    :return:
    '''
    f = open(file_name, 'w+', encoding = 'utf-8')
    print(len(content))
    f.writelines(content)
    f.close()

def get_parameters(cate):
    '''
    得到每种图片切分的两个界限
    :param cate:
    :return:
    '''
    cate_path = os.path.join(image_path+'/',cate)  # 每个图片种类的路径
    image_link = get_link(cate_path)   # 获取每个图片种类的名称列表
    image_num = len(image_link)
    image_tmp1 = int(image_num/10*6)
    image_tmp2 = int(image_num/10*8)
    # print(image_tmp1,image_tmp2)
    # print('before',image_link)
    random.shuffle(image_link)
    # print('after',image_link)
    return image_link,image_tmp1,image_tmp2

def imread(im_path, shape = SHAPE, color="RGB", mode=cv2.IMREAD_UNCHANGED):

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
        cate_path = os.path.join(image_path + '/', label)
        # print(cate_path)
        for image_name in images:
            # print(image_name)
            # print(i[0],i[1])
            labels.append(label)   # 这里的标签转为整数比较好处理，因为类别和数组下标是一致的
            image_list.append(image_name)

            # print(glob(i_path+'/'+image_name)[0])
            # print(cate_path,image_name)
            # print(cate_path,type(cate_path),image_name,type(image_name))
            cate_image_path = glob(cate_path+'/'+image_name+'.jpg')[0]
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

def pickled(savepath, data, label, fnames, pickle_name, bin_num=5, mode="train"):
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

    pass

def save_text(cate, train_, dev_, test_):
    '''
    将每类文本存储为训练集、验证集、预测集，比例为：6：2：2
    :param image_link:
    :param image_tmp1:
    :param image_tmp2:
    :return:
    '''
    responding_train_text = get_text(cate, train_)  # 根据图片获取相应文本
    responding_dev_text = get_text(cate, dev_)  # 根据图片获取相应文本
    responding_test_text = get_text(cate, test_)  # 根据图片获取相应文本
    print('=========Saving text of cate%s has started:=========='%cate)
    # print(len(responding_train_text))
    save_to_file('ans_text_0/train/%s.csv' % cate, responding_train_text)  # 将文本保存起来
    print('train_text is saved!')
    # print(len(responding_dev_text))
    save_to_file('ans_text_0/dev/%s.csv' % cate, responding_dev_text)  # 将文本保存起来
    print('dev_text is saved!')
    # print(len(responding_test_text))
    save_to_file('ans_text_0/test/%s.csv' % cate, responding_test_text)  # 将文本保存起来
    print('test_text is saved!')

if __name__ == '__main__':
    text_sort(text_path)    # 图片读取按名称大小顺序，所以文本也要排序后再读取
    train_images = []
    dev_images = []
    test_images = []
    for cate in text_list:
        cate = cate.split('.')[0]
        image_link, image_tmp1, image_tmp2 = get_parameters(cate)   # 图片名称随机列表和切分点
        train_,dev_,test_ = carve_image(image_link, image_tmp1, image_tmp2) # 切分后排好序的训练、验证和预测数据
        train_images.append([cate, train_])
        dev_images.append([cate, dev_])
        test_images.append([cate, test_])
        # save_text(cate, train_,dev_,test_)
    # print(train_images)
    save_image(train_images, dev_images, test_images)