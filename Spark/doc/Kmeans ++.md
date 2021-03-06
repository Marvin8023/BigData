# Kmeans

K-means++ 是一种为k-means聚类算法选择初始值的算法。是一种避免标准k—means算法有时候发现较弱聚的算法。

## 区别

1、kmeans，在算法最开始随机选取数据集中的k个点作为聚类中心。

2、kmeans++ 是按照一下方式选取k个聚类中心：
    
    假设已经选取了n个出事聚类中心(0<n<k)，则在选取第n+1个聚类中心时，距离当前n个聚类中心越远的点会有更高的概率被选为第n+1个聚类中心。第一个聚类中心依旧采用随机的方式选取。

## 算法步骤

1、从样本中随机选取k个样本作为聚类中心，
2、计算样本与聚类中心的距离，根据距离将样本划入相应的族，
3、计算族的均值作为新的聚类中心。
4、重复上述步骤。