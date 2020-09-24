#!/bin/sh
# LF    CR-LFだと動かない
SCRIPT_DIR=$(cd $(dirname ${BASH_SOURCE:-$0}); pwd)
CNF_DIR=${SCRIPT_DIR}/../configure
JDOC_DIR=../docs
source ${CNF_DIR}/configure.sh
TARGET=${hiMongo}
LIB_DIR=${SCRIPT_DIR}/../lib
hiMongoLIB=${LIB_DIR}/${hiMongoJAR}
hiNoteLIB=${LIB_DIR}/${hiNoteJAR}
mongoLIB=${LIB_DIR}/${mongoJAR}
LIBS=".:${hiNoteLIB}:${mongoLIB}"
# jdocセットのクリア
rm -rf ${JDOC_DIR}
mkdir ${JDOC_DIR}
mkdir ${JDOC_DIR}/img

# javadoc実施 (-link先がhttpのものが有ることに留意)
# 3.12はhttpでもhttpsでも上手く@linkされない
javadoc -public -link http://www.otsu.co.jp/OtsuLibrary/jdoc/ -link https://docs.oracle.com/javase/jp/8/docs/api/  -link http://mongodb.github.io/mongo-java-driver/3.7/javadoc/ --allow-script-in-comments -encoding UTF-8 -charset "UTF-8" -docencoding UTF-8 -d ${JDOC_DIR} -classpath ${LIBS} ${TARGET}.java

#http://mongodb.github.io/mongo-java-driver/3.7/javadoc/org/bson/Document.html?is-external=true
#https://mongodb.github.io/mongo-java-driver/3.12/javadoc/org/bson/Document.html

# GitHubのREADME.mdで使う画像
cp sample0.png ${JDOC_DIR}/img/

# cssのマージ
cat ${JDOC_DIR}/stylesheet.css ./hi_style.css > tmp.css
mv tmp.css ${JDOC_DIR}/stylesheet.css

# http参照にtarget="_blank" rel="noopener noreferrer"
convert_html() {
   echo -jar ${hiNoteLIB} conv -with replace.regex -in ${1} -out ${1}.out
   java -jar ${hiNoteLIB} conv -with replace.regex -in ${1} -out ${1}.out
   echo -jar ${hiNoteLIB} conv -with convDrivVer.regex -in ${1}.out -out ${1}
   java -jar ${hiNoteLIB} conv -with convDrivVer.regex -in ${1}.out -out ${1}
   rm -rf ${1}.out
   }
export hiNoteLIB
export -f convert_html
find ${JDOC_DIR} -name \*.html -exec bash -c 'convert_html "${1}"' -- {} \;

# 自ホスト上のwebサーバへ送る
rm -rf /var/www/html/hiMongo/* > /dev/null
cp -R ${JDOC_DIR}/* /var/www/html/hiMongo/

# 0_05->0_06 mongo-java-driver参照を3.7から3.12に変更
