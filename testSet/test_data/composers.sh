#!/bin/bash
mongo --quiet << 'EOT'
use db02
db.composer.drop()
db.composer.insertMany([
    {famiryName:'Bach',
     givenName:['Johann','Sebastian'],
     nationality:['独'],
     lifeTime:[1685,1750]},
    {famiryName:'Bartók',
     givenName:['Béla'],
     nationality:['ハンガリー','米'],
     lifeTime:[1881,1945]},
    {famiryName:'Beethoven',
     givenName:['Ludwig','van'],
     nationality:['独'],
     lifeTime:[1770,1827]},
    {famiryName:'Brahms',
     givenName:['Johannes'],
     nationality:['独'],
     lifeTime:[1833,1897]},

    {famiryName:'Chopin',
     givenName:['Frédéric-Françoic'],
     nationality:['ポーランド','仏'],
     lifeTime:[1810,1849]},

    {famiryName:'Dvořák',
     givenName:['Antonín'],
     nationality:['チェコ'],
     lifeTime:[1841,1904]},

    {famiryName:'Mahler',
     givenName:['Gustav'],
     nationality:['墺'],
     lifeTime:[1860,1911]},
    {famiryName:'Mozart',
     givenName:['Wolfgang','Amadeus'],
     nationality:['墺'],
     lifeTime:[1756,1791]},

    {famiryName:'Stravinsky',
     givenName:['Igor','Fydorovich'],
     nationality:['露','米'],
     lifeTime:[1756,1791]},

    {famiryName:'Tchaikovsky',
     givenName:['Peter','Ilyich'],
     nationality:['露'],
     lifeTime:[1840,1893]},

    {famiryName:'伊福部',
     givenName:['昭'],
     nationality:['日'],
     lifeTime:[1914,2006]}

   ])
db.composer.find({},{_id:0})
print("--- start with Ba --")
db.composer.find({famiryName:/^Ba/},{_id:0})
print("--- with ra or řá --")
db.composer.find({famiryName:/(ra|řá)/},{_id:0})
print("--- end with sky --")
db.composer.find({famiryName:/sky$/},{_id:0})

EOT
