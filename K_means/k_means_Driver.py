# -*- coding: utf-8 -*-     支持文件中出现中文字符
#########################################################################
#########################################################################

"""
Created on Sun Nov 3 13:45:42 2019

@author: yangmengyao

代码功能描述：  （1）读取数据
              （2）调用k均值聚类函数，得出分类结果
              （3）根据分类结果绘制图像
              （4）根据分类结果得出类内差异w(c)，类间距离b(c),聚类总体质量w(c)/b(c)
              （5）...

"""
#####################################################################
import csv
import codecs
from k_means import *
import numpy as np
import matplotlib.pyplot as plt
input_file = 'testcsv.csv'                                              # 读取数据
records = []
with codecs.open(input_file, 'r', "utf-8-sig") as f:
    f = csv.reader(f, delimiter=',')
    for line in f:
        r = [float(i) for i in line]
        records.append(r)
n_records = len(records)
dataSet = np.array([records[i][0:6]for i in range(len(records))])
k = int(input("请输入k值："))                                           # 输入分类组数
centroids, clusterAssment = KMeans(dataSet, k)                         # 调用之前定义函数，得出分类结果
showCluster(dataSet, k, centroids, clusterAssment)                     # 绘制分类结果
wc = np.sum((clusterAssment[:,1]))**2                                  # 计算类内差异w(c)
print("w(c)="+str(wc))
bc = 0                                                                 # 计算类间距离b(c)
for i in range(k):
    for j in range(i,k):
        bc += (distEclud(centroids[i,:], centroids[j,:]))**2
print('b(c)='+str(bc))
print("w(c)/b(c)="+str(wc/bc))                                        # 计算聚类总体质量w(c)/b(c)
