// use db01
db.famiryTree.drop()
db.famiryTree.createIndex({'name':1},{unique:true})
db.famiryTree.insertMany([
   {'name':'P0001', 'status':'-', 'father':'-', 'mother':'-'}
  ,{'name':'P0002', 'status':'-', 'father':'-', 'mother':'-'}
  ,{'name':'P0003', 'status':'KING', 'father':'-', 'mother':'-'}
  ,{'name':'P0004', 'status':'QUEEN', 'father':'-', 'mother':'-'}
  ,{'name':'P0005', 'status':'-', 'father':'-', 'mother':'-'}
  ,{'name':'P0006', 'status':'-', 'father':'-', 'mother':'-'}
  ,{'name':'P0007', 'status':'-', 'father':'-', 'mother':'-'}
  ,{'name':'P0008', 'status':'-', 'father':'-', 'mother':'-'}
  ,{'name':'P0009', 'status':'-', 'father':'P0001', 'mother':'P0002'}
  ,{'name':'P0010', 'status':'-', 'father':'P0003', 'mother':'P0004'}
  ,{'name':'P0011', 'status':'-', 'father':'P0003', 'mother':'P0008'}
  ,{'name':'P0012', 'status':'-', 'father':'P0005', 'mother':'P0006'}
  ,{'name':'P0013', 'status':'-', 'father':'P0005', 'mother':'P0008'}
  ,{'name':'P0014', 'status':'-', 'father':'P0007', 'mother':'P0008'}
  ,{'name':'P0016', 'status':'-', 'father':'P0007', 'mother':'P0008'}
  ,{'name':'P0017', 'status':'-', 'father':'P0009', 'mother':'P0010'}
  ,{'name':'P0018', 'status':'-', 'father':'P0011', 'mother':'P0012'}
  ,{'name':'P0020', 'status':'-', 'father':'P0013', 'mother':'P0014'}
  ,{'name':'P0021', 'status':'-', 'father':'P0013', 'mother':'P0014'}
  ,{'name':'P0022', 'status':'-', 'father':'P0009', 'mother':'P0016'}
  ,{'name':'P0024', 'status':'-', 'father':'P0009', 'mother':'P0016'}
  ,{'name':'P0025', 'status':'-', 'father':'P0009', 'mother':'P0016'}
  ,{'name':'P0027', 'status':'-', 'father':'P0021', 'mother':'P0022'}
  ,{'name':'P0028', 'status':'-', 'father':'P0021', 'mother':'P0018'}
  ,{'name':'P0029', 'status':'-', 'father':'P0025', 'mother':'P0024'}
  ,{'name':'P0030', 'status':'-', 'father':'P0017', 'mother':'P0020'}
  ,{'name':'P0031', 'status':'-', 'father':'P0021', 'mother':'P0022'}
  ,{'name':'P0032', 'status':'-', 'father':'P0011', 'mother':'P0020'}
  ,{'name':'P0033', 'status':'-', 'father':'P0025', 'mother':'P0024'}
   ])
db.famiryTree.find()

