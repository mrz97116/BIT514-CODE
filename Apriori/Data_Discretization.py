# -*- coding: utf-8 -*-     支持文件中出现中文字符
#########################################################################
#########################################################################

"""
Created on Wed Nov 13 13:45:42 2019

@author: yangmengyao

代码功能描述：  （1）读取数据（testcsv.csv）
              （2）根据获得的最佳k,其中k=[2，4，2，2，3，2]对每个属性进行k-means分类，实现数据离散化
              （3）绘制k-means分类结果
              （4）将数据离散化结果保存为csv文件，命名为new.csv，供Apriori_Driver使用
              （5）...

"""
#####################################################################

import numpy as np
import matplotlib.pyplot as plt
from sklearn.cluster import KMeans
import csv
import codecs
import pandas as pd

input_file = 'testcsv.csv'                                              # 读取数据
records = []
with codecs.open(input_file, 'r', "utf-8-sig") as f:
    f = csv.reader(f, delimiter=',')
    for line in f:
        r = [float(i) for i in line]
        records.append(r)
n_records = len(records)
dataSet = np.array([records[i][0:6]for i in range(len(records))])
# print(dataSet.shape[1])

y = np.array([0 for i in range(n_records)])
k = [2, 4, 2, 2, 3, 2]                                                       # 数据来源肘部算法图
s = ['a', 'b', 'c', 'd', 'e', 'f']
# s = [abcdef]
r = [[' ' for i in range(dataSet.shape[1])] for j in range(n_records)]
# print(r)
for i in range(0,6):
    x = dataSet[:, i]
    data = np.array(list(zip(x, y)))
    # 绘制散点图及聚类结果中心点
    plt.figure()
    # plt.axis([0, 10, 0, 10])
    plt.grid(True)
    plt.plot(x, y, 'k.')
    kmeans = KMeans(n_clusters=k[i])
    kmeans.fit(data)
    plt.plot(kmeans.cluster_centers_[:, 0], kmeans.cluster_centers_[:, 1], 'r.')
    plt.show()
    res = list(kmeans.labels_)
    for j in range(n_records):
        r[j][i] = s[i] + str(res[j])
# my_matrix = np.array(r)
DD = pd.DataFrame(r)
DD.to_csv("new.csv", index=False, header=False)