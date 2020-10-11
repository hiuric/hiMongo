#!/bin/sh
# hiMongoのライブラリ、バージョン設定
# src,testSet,sampleSetから共通に呼ばれる
# mongoDBのログを抑止するためのだけライブラリは含んでいない
export hiMongo=hiMongo
export hiMongoVER=_0_09
export hiMongoJAR=${hiMongo}${hiMongoVER}.jar
export hiNoteJAR=hiNote_3_10.jar
export mongoJAR=mongo-java-driver-3.12.5.jar
# 注意！ 改行=LF
