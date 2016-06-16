import eu.mihosoft.vrl.v3d.parametrics.*;

ArrayList<CSG> makeHead(){
	//Set up som parameters to use
	LengthParameter thickness 		= new LengthParameter("Material Thickness",3.5,[10,1])
	LengthParameter headDiameter 		= new LengthParameter("Head Dimeter",100,[200,50])
	LengthParameter snoutLen 		= new LengthParameter("Snout Length",headDiameter.getMM(),[200,50])
	LengthParameter jawHeight 		= new LengthParameter("Jaw Height",50,[200,10])
	LengthParameter JawSideWidth 		= new LengthParameter("Jaw Side Width",20,[40,10])

	CSG mechPlate =new Cylinder(	headDiameter.getMM()/2,
							headDiameter.getMM()/2,
							thickness.getMM(),(int)30).toCSG() // a one line Cylinder
							.scalex(2*snoutLen.getMM()/headDiameter.getMM())
							
	CSG bottomJaw = mechPlate.difference(
		mechPlate
		.scalex(-0.6)
		.scaley(-0.6)
		.scalez(5)
		)
		.intersect(new Cube(
			snoutLen.getMM()+JawSideWidth.getMM(),
			headDiameter.getMM(),
			thickness.getMM()*2)
			.noCenter()
			.toCSG()
			.movey(- headDiameter.getMM()/2)
			.movex(- JawSideWidth.getMM())
			)
					
	
	mechPlate=mechPlate 
		.movez(jawHeight.getMM())

	CSG sideJaw = new Cube(
			JawSideWidth.getMM(),
			thickness.getMM(),
			jawHeight.getMM()+thickness.getMM()
			).toCSG()
			.movez(jawHeight.getMM()/2 +thickness.getMM()/2 )
	CSG LeftSideJaw =sideJaw
			.movey(
				headDiameter.getMM()/2
			) 
	
		
	def returnValues = 	[mechPlate,bottomJaw,LeftSideJaw]
	for (int i=0;i<returnValues.size();i++){
		int index = i
		returnValues[i] = returnValues[i]
		.setParameter(thickness)
		.setParameter(headDiameter)
		.setParameter(snoutLen)
		.setParameter(jawHeight)
		.setRegenerate({ makeHead().get(index)})
	}
	return returnValues
}
CSGDatabase.clear()//set up the database to force only the default values in			
return makeHead();