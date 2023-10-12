################################################
'''
功能：分别将训练、预测、验证集下的文本收集到一个文本文件中，供4.2使用
'''
################################################

from os import listdir
from glob import glob

train_path = 'ans_text_0/train'
dev_path = 'ans_text_0/dev'
test_path = 'ans_text_0/test'
text_list = ['animals.csv','architecture.csv','art.csv','cars_motorcycles.csv','celebrities.csv']

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

def collect_text(root_path):
    '''
    收集对应目录下所有文本，压缩到一个文件夹中
    :param root_path:
    :return:
    '''
    final_text = []
    for cate in text_list:
        cate_path = glob(root_path + '/' + cate)[0]
        f = open(cate_path, 'r+', encoding='utf-8')
        content = f.readlines()
        final_text.extend(content)
        f.close()
    print(final_text)
    save_to_file('bin/'+root_path.split('/')[1]+'.csv', final_text)

if __name__ == '__main__':

    collect_text(train_path)
    collect_text(dev_path)
    collect_text(test_path)