
# coding: utf-8

# In[41]:


import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn import preprocessing
import tensorflow as tf


# In[43]:


train_data = pd.read_csv("customer_investment.csv")


# In[6]:


encoder = preprocessing.LabelEncoder()


# In[7]:


req = encoder.fit_transform(train_data['INVESTMENTTYPE'])


# In[8]:


earr = encoder.classes_


# In[9]:


train_data['INVESTMENTTYPE'] = req


# In[10]:


train_y_labels = train_data['INVESTMENTTYPE']


# In[11]:


train_x_data = train_data.drop(['INVESTMENTTYPE'],axis=1)


# In[12]:


X_train, X_test, y_train, y_test = train_test_split(train_x_data,train_y_labels,test_size=0.3,random_state=101)

#X_test = [30,Salarised]

X_test = {
        'AGE': [70],
        'CATEGORY': ['salarised'],
    }
X_test=pd.DataFrame(X_test)
	
	
	
# In[26]:


org = tf.feature_column.categorical_column_with_hash_bucket("CATEGORY", hash_bucket_size=52)
amount = tf.feature_column.numeric_column("AGE")


# In[27]:


feat_cols = [org,amount]


# In[28]:


input_func=tf.estimator.inputs.pandas_input_fn(x=X_train,y=y_train,batch_size=100,num_epochs=None,shuffle=True)


# In[29]:


model = tf.estimator.LinearClassifier(feature_columns=feat_cols,n_classes = 10)


# In[30]:


model.train(input_fn=input_func,steps=5000)


# In[31]:


eval_input_func = tf.estimator.inputs.pandas_input_fn(x = X_train, y = y_train,
                                                     batch_size = 10, num_epochs=1,
                                                     shuffle=False)


# In[32]:


results = model.evaluate(eval_input_func)


# In[33]:


results


# In[34]:


pred_fn = tf.estimator.inputs.pandas_input_fn(x=X_test,batch_size=len(X_test),shuffle=False)


# In[35]:


predictions = list(model.predict(input_fn=pred_fn))


# In[36]:


predictions


# In[37]:

final_preds=[]
#invType = earr[predictions['class_ids'][0]]
for pred in predictions:
    row = {}
    row['INVESTMENTTYPE'] = earr[pred['class_ids'][0]]
    final_preds.append(row)
    print(row)
dataframe = pd.DataFrame.from_dict(final_preds)
dataframe.to_csv('PREDICTION.csv', index = False)
