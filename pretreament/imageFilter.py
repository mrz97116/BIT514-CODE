################################################
'''
功能：图片过滤
'''
################################################

from os import *
import numpy as np
from glob import glob
import cv2
from pickled import *
from os import listdir


def FindExamAllFiles(path):
    '''
    得到path目录下所有图片的路径
    :param path:
    :return:
    '''
    tmp = []
    for root, dirs, files in walk(path):
        # print(root)
        for filepath in files:
            # print(filepath)
            # imgFileFullPath = path.join(root, filepath)
            imgFileFullPath = glob(root+'\\'+filepath)[0]
            print('ifp:'+ imgFileFullPath)
            if imgFileFullPath.endswith('.jpg'):
                tmp.append(imgFileFullPath)
    return tmp

def resave_image(rootPath):
    '''
    将图片读取后重新保存，滤去多于信息
    :return:
    '''
    # magick.exe所在的路径
    # commandTool = os.getcwdu()+os.sep+"tools"+os.sep+'magick.exe'
    commandTool = 'E:/ImageMagick-7.0.7-Q16/magick.exe'  # 找到自己的ImageMagick绝对路径
    # commandTool = '/usr/bin/convert'
    pngs = []
    classes = listdir(rootPath)  # 获取Path下文件类型名称
    print(classes)
    for i in classes:
        # i_path = path.join('E:\MyWork\PycharmProjects\interest_prediction\media\\'+rootPath, '\\'+i)  # 第i个图片的相对地址
        i_path = rootPath
        # i_path = glob(rootPath+i+'/')[dia0]
        print('i_path:' + i_path)
        pngPathList = FindExamAllFiles(i_path)
        pngs.extend(pngPathList)
    # for i in pngs:print(i)

    for pngPath in pngs:
        # 拼凑cmd命令

        command = "{0} {1} {2}".format(commandTool, pngPath, pngPath)
        os.system(command)

def imread(im_path):
    # print('path',im_path) # 打印图片路径
    im = np.float32(cv2.imread(im_path,1))
    im = np.array(im)
    # print("图片长、宽、通道数：",im.shape)  # 打印图片形状
    # print(im.shape)
    if(im.shape == ()) :
        print("非法图片：",im_path)
        os.remove(im_path)
    elif(im.shape[0] < 32 or im.shape[1] < 32):
        print('Picture is too small',im_path)
        os.remove(im_path)

def del_illegal_image(rootPath):
    DATA_LEN = 3072
    CHANNEL_LEN = 1024
    SHAPE = 32
    Picture_sum = 1000
    for cate in listdir(rootPath):
        cate_path = glob(rootPath+cate)[0]
        for image in listdir(cate_path):
            image_path = glob('E:\MyWork\PycharmProjects\interest_prediction\media\\'+cate_path+'\\'+image)[0]
            print(image_path)
            imread(image_path)

if __name__ == "__main__":
    rootPath = "E:/MyWork/PycharmProjects/interest_prediction/media"
    resave_image(rootPath)
    # del_illegal_image(rootPath)