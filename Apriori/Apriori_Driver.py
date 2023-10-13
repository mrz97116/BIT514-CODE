# -*- coding: utf-8 -*-     支持文件中出现中文字符
#########################################################################
#########################################################################

"""
Created on Wed Nov 13 13:45:42 2019

@author: yangmengyao

代码功能描述：  （1）读取数据（new.csv）
              （2）根据支持度阈值（min_support_degree = 0.4）生成频繁项集（有剪枝）
              （3）根据置信度阈值（min_conf_degree = 0.7）生成规则
              （4）...

"""
#####################################################################

import numpy as np
import csv
import codecs

input_file = 'new.csv'                                     # 读取数据
records = []
with codecs.open(input_file, 'r', "utf-8-sig") as f:
    f = csv.reader(f, delimiter=',')
    for line in f:
        r = [str(i) for i in line]
        records.append(r)
n_records = len(records)
dataSet = np.array([records[i][0:6]for i in range(len(records))])
# print(dataSet)
r=[]
min_support_degree = 0.4                                 # 支持度阈值
min_conf_degree = 0.7                                    # 置信度阈值
k = [2, 4, 2, 2, 3, 2]                                   # 根据肘部算法求得每个属性k-mean分类的最佳k
attr_name_list = ['a', 'b', 'c', 'd', 'e', 'f']          # 区分属性：例属性1的两类为a0,a1;属性2的四类为b0,b1,b2,b3
attr_list = [[attr_name_list[i] + str(j) for j in range(k[i])] for i in range(6)]  # 每个属性的类别列表的集合，二维list
min_support_num = int(n_records * min_support_degree)    # 最小支持度计数
freq_class_list = []                                     # 每一步后得到的频繁项集
for attr_index in range(6):
    this_attr_classes = list(set([tuple(dataSet[i][:(attr_index+1)]) for i in range(n_records)]))  # 获取前attr_index个属性的候选项集
    del freq_class_list[:]                               # 清空频繁项集，为下一步生成频繁项集作准备
    this_attr_classes_len = len(this_attr_classes)       # 该类别列表长度
    this_elements_list = [[list(dataSet[i]) for i in range(n_records) if all(dataSet[i][:(attr_index+1)] == this_attr_classes[j])] for j in range(this_attr_classes_len)]  # 将记录按照候选项集分类
    this_attr_num = [len(this_elements_list[i]) for i in range(this_attr_classes_len)]  # 每个类别的频数列表
    freq_class_num_list = [num for num in this_attr_num if num >= min_support_num]      # 计算符合支持度的频繁项集的频数集合（可能有重复元素）
    freq_class_index_list = list(set([i for i, num in enumerate(this_attr_num) if num in freq_class_num_list]))  # 根据频数集合去重得到频繁项下标
    freq_class_list.append([this_attr_classes[freq_class_index] for freq_class_index in freq_class_index_list])  # 根据频繁项下标得到频繁项集
    r.append(freq_class_num_list[0])
freq_class_list = list(freq_class_list[0][0])
print('频繁项集为：' + str(freq_class_list))
conf_degree_list = [r[5]/r[4-i] for i in range(5)]       # 算根据频繁项集产生关联规则的置信度
rule_index = [i for i, x in enumerate(conf_degree_list) if x >= min_conf_degree]  # 根据置信度阈值生成规则
print('生成规则如下：')
for i in rule_index:
    print(''.join(freq_class_list[:(5 - i)])+'->'+''.join(freq_class_list[(5 - i):])+'    置信度为'+str(conf_degree_list[i]))

