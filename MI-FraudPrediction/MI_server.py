#!/usr/bin/python3
from flask import Flask, request, jsonify
from flask_restful import Resource, Api
from sqlalchemy import create_engine
from json import dumps

import pandas as pd
import numpy as np
from sklearn.model_selection import train_test_split
from sklearn import preprocessing
import tensorflow as tf
import os
#import csvmapper



db_connect = create_engine('sqlite:///chinook.db')
app = Flask(__name__)
api = Api(app)


class InvestmentType(Resource):
    def get(self, age_group , category):
        os.system('python MI_prediction.py')
        data = pd.read_csv("PREDICTION.csv")
        #converter = csvmapper.JSONConverter(data)
        #print(converter.doConvert(pretty=True))
        #print(data)
        return jsonify(data.to_json())
            
    

api.add_resource(InvestmentType, '/invt/<age_group>/<category>') # Route_4


if __name__ == '__main__':
     app.run()
