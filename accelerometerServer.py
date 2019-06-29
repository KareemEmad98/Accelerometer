import socket
import numpy as np
import os
from sklearn.model_selection import train_test_split
from sklearn.neighbors import KNeighborsClassifier
from sklearn.preprocessing import  StandardScaler
import numpy as np
from sklearn.metrics import confusion_matrix
from sklearn.metrics import f1_score
from sklearn.metrics import accuracy_score
import pandas as pd
from plyer import accelerometer
# shehab code
array=[]
count = 0
filenumber = 0;
array=[]
df = pd.DataFrame()
x=0
spath = "C:\\Users\\PC\\PycharmProjects\\task\\gestures-dataset"
for dirName, subdirList, fileList in os.walk(spath, topdown=False):
   # print('Found directory: %s' % dirName)
    os.chdir(dirName)
    # x+=1
    # print(x)
    filenumber+=1

    if filenumber>20:
        filenumber=0
    for fname in fileList:
        # if (count == 20):
            # if (filenumber > 20):
            #     filenumber = 19
            # filenumber += 1
            # count = 0
            # break

        # x+=1
        # print(x)
        data = pd.read_csv(fname, sep=" ", header=None)
        data.columns = ["a", "b", "c", "x", "y", "z"]
        #data.rows = ['a']
        del data['a']
        del data['b']
        del data['c']
        # print(filenumber)
        myclass = filenumber
        array.append(filenumber)
        data['class'] = myclass
        # filenumber+=1
        df_out = data.set_index(['class', data.groupby(['class']).cumcount() + 1]).unstack().sort_index(level=1, axis=1)
        df_out.columns = df_out.columns.map('{0[0]}_{0[1]}'.format)
        df_out.reset_index()
        count+=1
        # df = df.append(data.iloc[:, 0:3])
        #newd=df_out.loc[:,:'f_4']
        #print(newd)
        df=df.append(df_out,sort=True)
# df=df.fillna(0)
# print(len(fileList))

df=df.dropna(axis='columns')
print(df)
x_train,x_test,y_train,y_test=train_test_split(df,array,test_size=0.2,random_state=0)
knn=KNeighborsClassifier(n_neighbors=7)
knn.fit(x_train,y_train)
print ("accuacy",knn.score(x_test,y_test))
#end shehab code
serv = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serv.bind(('192.168.1.3', 8012))
serv.listen(5)
from_client=""
counter=0
frameNumber=10
while True:
    conn, addr = serv.accept()
    # print('Gesture Sent')


    while True:
        data = conn.recv(4096)

        if not data:
            break
        counter += 1
        from_client+=str(data)
        # print(from_client)
    # print(counter)
    if counter==frameNumber:

        from_client=from_client.replace('b',' ')
        from_client = from_client.replace("'", ' ')
        from_client = from_client.replace('"', ' ')
        from_client=from_client.split(',')
        # x = np.array(from_client)
        del from_client[len(from_client)-1]
        # y = x.astype(np.float)
        print(len(from_client))
        #classification
        # example = np.array(from_client)
        # example = example.reshape(1, -1)
        ex=np.array(from_client)
        ex=ex.reshape(1,-1)
        prediction = knn.predict(ex)
        print('prediction: %s' % prediction)
        # serv.sendto(prediction,)
        #End classification
        from_client=""
        prediction=""
        counter=0
conn.close()
print('client disconnected')


