#!/bin/sh
# hiMongoのライブラリ、バージョン設定
# src,testSet,sampleSetから共通に呼ばれる
# mongoDBのログを抑止するためのだけライブラリは含んでいない
export hiMongo=hiMongo
export hiMongoVER=_0_05
export hiMongoJAR=${hiMongo}${hiMongoVER}.jar
export hiNoteJAR=hiNote_3_09.jar
export mongoJAR=mongo-java-driver-3.12.5.jar
# 注意！ 改行=LF
