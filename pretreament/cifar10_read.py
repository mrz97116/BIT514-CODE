import pickle
import numpy as np

def unpickle(file):
    with open(file, 'rb') as fo:
      dict = pickle.load(fo,encoding='latin-1')
      print(dict)
      #############################################
      # todo :图片展示
      print(len(dict['data']),len(dict['data'][0]))
      print(dict['labels'])
      # for i in dict['data'][1]:
      #     print(i)
      ############################################
      # print(len(dict),len(dict[10]))
      # for i in dict[0]:print(i)
      ############################################
    return dict

if __name__ == '__main__':
  # ans = unpickle('./ans_texts/train_feat.pkl')
  ans = unpickle('./ans_images/train/train_batch_4')