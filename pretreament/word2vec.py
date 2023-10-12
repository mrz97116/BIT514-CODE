################################################
'''
功能：使用网上公开的语料库glove_model.txt，基于开源工具gensim的word2vec实现，
将自己的文本转化为词向量表达的形式
'''
################################################

# coding = 'utf-8'
import gensim
import os
import numpy as np
import pickle

model = gensim.models.KeyedVectors.load_word2vec_format("glove_model.txt",binary=False,encoding='utf-8')  # 删除binary=True
# print(model.similarity('woman','man'))
print(model['man'])

# train_path = 'bin/train.csv'
# dev_path = 'bin/dev.csv'
test_path = 'bin/test.csv'
# train_list = ['animals.csv','architecture.csv','art.csv','cars_motorcycles.csv','celebrities.csv']
error_sum = 0
iter_sum = 0

def save_to_pkl(file_name, content):
    '''
    保存到对应的pkl文件中
    :param file_name:
    :param content:
    :return:
    '''
    file = open(file_name, 'wb')
    pickle.dump(content, file)
    file.close()


def get_vector(text_path):
    '''
    讲对应文本转化为词向量
    :param root_path:
    :return:
    '''
    global error_sum,iter_sum
    sum = 0
    error = 0
    cate_vector = []
    f = open(text_path,'r+',encoding='utf-8')
    content = f.readlines()
    for senten in content:
        senten = senten.split('\n')[0].split(' ')
        senten_vec = np.zeros((1,300))
        for word in senten:
            try:
                word_vec = model[word]
                senten_vec = senten_vec+word_vec
                print(len(word_vec),type(word_vec))
            except:
                print('exception')
                error = error+1
            sum = sum+1
        cate_vector.extend(senten_vec)
    f.close()
    cate_vector = np.array(cate_vector)
    print(cate_vector)
    save_to_pkl(text_path.split('.')[0]+'_feat.pkl',cate_vector)
    error_sum = error_sum+error
    iter_sum = iter_sum+sum
    print('error',error)
    print('sum',sum)

if __name__ == '__main__':
    # get_vector(train_path)
    # get_vector(dev_path)
    get_vector(test_path)
    print(error_sum)
    print(iter_sum)