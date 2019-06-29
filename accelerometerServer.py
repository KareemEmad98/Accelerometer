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
count = 0
filenumber = 1;
array = []
df = pd.DataFrame()
df1 = pd.DataFrame()
spath = "C:\Users\HP\PycharmProjects\untitled\gestures-dataset"
for dirName, subdirList, fileList in os.walk(spath, topdown=False):
    # print('Found directory: %s' % dirName)
    os.chdir(dirName)
    for fname in fileList:
        # print fname
        if (filenumber > 20):
            filenumber = 1
        data = pd.read_csv(fname, sep=" ", header=None, nrows=10)
        data.columns = ["a", "b", "c", "d", "e", "f"]
        # data.rows = ['a']
        del data['a']
        del data['b']
        del data['c']
        myclass = filenumber
        data['class'] = myclass
        filenumber += 1
        df = df.append(data.iloc[:, 0:3])
        # df1=df1.append(data['class'])
        array = np.append(array, data['class'])
        x_train, x_test, y_train, y_test = train_test_split(df, array, test_size=0.32, random_state=42)
        knn = KNeighborsClassifier(n_neighbors=2)
        knn.fit(x_train, y_train)
        print("accuacy", knn.score(x_test, y_test))
#end shehab code
serv = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
serv.bind(('192.168.1.3', 8012))
serv.listen(5)
from_client=""
counter=0
frameNumber=13
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
        # df = df.fillna(0)
        example = [[0.919373, -1.072602, 10.113108], [2.145205, -0.306458, 10.419566]]
        prediction = knn.predict(example)
        print('prediction: %s' % prediction)
        
        # print(from_client)
        #End classification
        from_client=""
        prediction=""
        counter=0
conn.close()
print('client disconnected')



