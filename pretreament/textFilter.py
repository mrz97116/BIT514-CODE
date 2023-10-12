################################################
'''
功能描述：对文本过滤
功能一：找到图片对应的文本并重新保存起来
'''
################################################
from os import listdir
from glob import glob


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
# f = open('texts/architecture.csv','r+',encoding='utf-8')
# a = f.readlines()
# del a[animals]
# print(a)
# f.close()
#
# f = open('texts/architecture.csv','w+',encoding='utf-8')
# f.writelines(a)
# f.close()

# 读取非法文件下标
# import pickle
# f = open('bin/index.pkl','rb')
# content = pickle.load(f)    # list类型
# f.close()

# 对csv进行排序

def save_to_file(file_name,content):
    '''
    将content保存在文件中
    :param file_name:
    :param content:
    :return:
    '''
    f = open(file_name, 'w+', encoding = 'utf-8')
    # print(content)
    f.writelines(content)
    f.close()

def text_sort(text_path):
    '''
    对文本目录下所有文本按照图片名称进行排序
    :param text_path:
    :return:
    '''
    for file in listdir(text_path):
        file_path = glob(text_path+'/'+file)[0]
        print('file',file_path)
        f = open(file_path,'r+',encoding='utf-8')
        content = f.readlines()
        content = sorted(content,key=lambda i: (i.strip().split(',')[3]) if len(i.strip().split(',')) > 3 else 'animals')
        # f.writelines(content) 没关文件就直接写会加在后面而不是更新文件
        f.close()
        save_to_file(file_path,content)

def get_link(cate_path):
    '''
    获取该图像目录下图片列表
    :param image_path:
    :return:
    '''
    image_link = []
    for image_name in listdir(cate_path):
        link = image_name.split('.')[0] # str
        # print(link)
        image_link.append(link)
    return image_link

def get_text(text_path,image_link):
    '''
    根据图片列表获得对应文本信息
    :param text_path:
    :param image_link:
    :return:
    '''
    i = 0
    text_content = []
    image_num = len(image_link)
    print(image_num)
    # for cate in listdir(text_path):
    #     text_name = glob(text_path+'/'+cate)[animals]
    f = open(text_path,'r+',encoding='utf-8')
    content = f.readlines()
    print(len(content))
    for line in content:
        if(len(line.strip().split(',')) > 3):
            if(line.strip().split(',')[3] == image_link[i]):
                print('第%d个图片--%s--对应的文本:\n' % (i, image_link[i]),line)
                text_content.append(line)
                i = i + 1
                if(i == image_num):return text_content
    f.close()

if __name__ == '__main__':
    image_root = 'Pictures'
    text_root = 'Pictures_text'
    text_sort(text_root)    # 图片读取按名称大小顺序，所以文本也要排序后再读取

    for cate in listdir(image_root):
        cate_path = glob(image_root+'/'+cate)[0]   # 每个图片种类的路径
        image_link = get_link(cate_path)   # 获取每个图片种类的名称列表
        # print(image_link)
        text_path = glob(text_root+'/'+cate+'.csv')[0]
        print(text_path)
        responding_text = get_text(text_path,image_link)    # 根据图片获取相应文本
        print(responding_text)
        save_to_file(text_path,responding_text)    # 将文本保存起来
