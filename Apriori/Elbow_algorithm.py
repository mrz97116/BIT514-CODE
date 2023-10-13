# -*- coding: utf-8 -*-     支持文件中出现中文字符
#########################################################################
#########################################################################

"""
Created on Wed Nov 13 13:45:42 2019

@author: yangmengyao

代码功能描述：  （1）读取数据（testcsv.csv）
              （2）绘制每个属性的肘部算法图
              （3）根据肘部算法图，获得k=[2，4，2，2，3，2]
              （4）...

"""
#####################################################################

import numpy as np
import matplotlib.pyplot as plt
from sklearn.cluster import KMeans
from scipy.spatial.distance import cdist
import csv
import codecs


input_file = 'testcsv.csv'                                              # 读取数据
records = []
with codecs.open(input_file, 'r', "utf-8-sig") as f:
    f = csv.reader(f, delimiter=',')
    for line in f:
        r = [float(i) for i in line]
        records.append(r)
n_records = len(records)
dataSet = np.array([records[i][0:6]for i in range(len(records))])

y = np.array([0 for i in range(n_records)])                           # 绘制每个属性的肘部算法图
for i in range(0,6):
    x = dataSet[:, i]
    data = np.array(list(zip(x, y)))
    # 肘部法则 求解最佳分类数
    # K-Means参数的最优解也是以成本函数最小化为目标
    # 成本函数是各个类畸变程度（distortions）之和。每个类的畸变程度等于该类重心与其内部成员位置距离的平方和
    aa = []
    K = range(1, 10)
    for k in range(1, 10):
        kmeans = KMeans(n_clusters=k)
        kmeans.fit(data)
        aa.append(sum(np.min(cdist(data, kmeans.cluster_centers_, 'euclidean'), axis=1)) / data.shape[0])
    plt.figure()
    plt.plot(np.array(K), aa, 'bx-')
    plt.show()