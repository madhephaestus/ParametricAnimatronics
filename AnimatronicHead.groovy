import eu.mihosoft.vrl.v3d.parametrics.*;

CSG tSlotTabs(){
	LengthParameter boltDiam 		= new LengthParameter("Bolt Diameter",2.5,[8,2])
	LengthParameter nutDiam 		= new LengthParameter("Nut Diameter",4,[10,3])
	LengthParameter thickness 		= new LengthParameter("Material Thickness",3.5,[10,1])
	CSG tab =new Cube( thickness,
			thickness,
			thickness)
			.toCSG()
	double tabOffset  = boltDiam.getMM()+thickness.getMM()
	tab = tab
		.movey(-tabOffset)
		.union(tab
		.movey(tabOffset))
	return tab.toZMin()
}

CSG tSlotTabsWithHole(){
	LengthParameter boltDiam 		= new LengthParameter("Bolt Diameter",2.5,[8,2])
	LengthParameter thickness 		= new LengthParameter("Material Thickness",3.5,[10,1])
	
	return tSlotTabs()
			.union(
				new Cube( thickness,
			boltDiam,
			thickness)
			.toCSG()
			.toZMin())
}

CSG tSlotNutAssembly(){
	LengthParameter boltDiam 		= new LengthParameter("Bolt Diameter",2.5,[8,2])
	LengthParameter nutDiam 		= new LengthParameter("Nut Diameter",4,[10,3])
	LengthParameter thickness 		= new LengthParameter("Material Thickness",3.5,[10,1])
	LengthParameter nutThick 		= new LengthParameter("Nut Thickness",2,[10,3])
	CSG bolt = new Cube( thickness.getMM(),
			boltDiam.getMM(),
			thickness.getMM()*3)
			.toCSG()
			.toZMin()
	CSG nut =new Cube( thickness,
			nutDiam,
			nutThick)
			.toCSG()
			.toZMin()
			.movez(thickness.getMM()*2)
	return bolt.union(nut)
	
			
}

ArrayList<CSG> makeHead(){
	//Set up som parameters to use
	LengthParameter thickness 		= new LengthParameter("Material Thickness",3.5,[10,1])
	LengthParameter headDiameter 		= new LengthParameter("Head Dimeter",100,[200,50])
	LengthParameter snoutLen 		= new LengthParameter("Snout Length",headDiameter.getMM(),[200,50])
	LengthParameter jawHeight 		= new LengthParameter("Jaw Height",50,[200,10])
	LengthParameter JawSideWidth 		= new LengthParameter("Jaw Side Width",20,[40,10])
	LengthParameter boltDiam 		= new LengthParameter("Bolt Diameter",2.5,[8,2])
	LengthParameter nutDiam 		= new LengthParameter("Nut Diameter",4,[10,3])
	LengthParameter nutThick 		= new LengthParameter("Nut Thickness",2,[10,3])

	String jawServoName = "standard"
	
	double jawAttachOffset =  (headDiameter.getMM()/2
				-thickness.getMM()/2 
				-thickness.getMM())
	HashMap<String,Object> servoConfig =(HashMap<String,Object>) ScriptingEngine
	                    .gitScriptRun(
                                "https://github.com/madhephaestus/Hardware-Dimensions.git", // git location of the library
	                              "json/hobbyServo.json" , // file to load
	                              null
                        )
	HashMap<String,Object> jawServoConfig = servoConfig.get(jawServoName)
	double servoHeightFromMechPlate = jawServoConfig.get("servoThinDimentionThickness")/2
	double servoJawMountPlateOffset = jawServoConfig.get("tipOfShaftToBottomOfFlange")
	
	CSG jawServo = (CSG)ScriptingEngine
	                    .gitScriptRun(
                                "https://gist.github.com/3f9fef17b23acfadf3f7.git", // git location of the library
	                              "servo.groovy" , // file to load
	                              [jawServoName]
                        )
                        .toZMax()
                        .roty(90)
                        .rotz(90)
                        .movez(	jawHeight.getMM() 
                       		 	+thickness.getMM()
                       		 	+servoHeightFromMechPlate
                        )
                        .movey(jawAttachOffset+thickness.getMM()/2)
                        .setColor(javafx.scene.paint.Color.CYAN)
                        
	CSG smallServo = (CSG)ScriptingEngine
	                    .gitScriptRun(
                                "https://gist.github.com/3f9fef17b23acfadf3f7.git", // git location of the library
	                              "servo.groovy" , // file to load
	                              ["towerProMG91"]
                        )
                        

	CSG baseHead =new Cylinder(	headDiameter.getMM()/2,
							headDiameter.getMM()/2,
							thickness.getMM(),(int)30).toCSG() // a one line Cylinder
													
	CSG mechPlate	=baseHead.scalex(2*snoutLen.getMM()/headDiameter.getMM())
							.intersect(new Cube(
								snoutLen.getMM()+JawSideWidth.getMM(),
								headDiameter.getMM(),
								thickness.getMM()*2)
								.noCenter()
								.toCSG()
								.movey(- headDiameter.getMM()/2)
								.movex(- JawSideWidth.getMM())
								.union(baseHead)
								.hull()
			)
							
	CSG bottomJaw = mechPlate.difference(
		mechPlate
		.scalex(-0.8)
		.scaley(-0.8)
		.scalez(5),
		new Cube(
			snoutLen.getMM()+JawSideWidth.getMM(),
			headDiameter.getMM(),
			thickness.getMM()*2)
			.noCenter()
			.toCSG()
			.toXMax()
			.movey(- headDiameter.getMM()/2)
			.movex(- JawSideWidth.getMM())
			
		)
		
					
	
	mechPlate=mechPlate 
		.movez(jawHeight.getMM())
	
	
	CSG sideJaw = new Cube(
			JawSideWidth.getMM(),
			thickness.getMM(),
			jawHeight.getMM()+thickness.getMM()
			+servoHeightFromMechPlate
			).toCSG()
			.toZMin()
			.union(new Cylinder(	JawSideWidth.getMM()/2,
								JawSideWidth.getMM()/2,
								thickness.getMM(),(int)30).toCSG()
					.movez(-thickness.getMM()/2)
					.rotx(90)
					.movez(jawHeight.getMM() +thickness.getMM()+servoHeightFromMechPlate )
							)

				
	CSG LeftSideJaw =sideJaw
			.movey(jawAttachOffset) 
			.difference(jawServo)
	CSG RightSideJaw =sideJaw
			.movey(-jawAttachOffset) 

	mechPlate = mechPlate.difference(LeftSideJaw.scalex(1.8),RightSideJaw.scalex(1.8))
	bottomJaw = bottomJaw.difference(LeftSideJaw,RightSideJaw)
		
	def returnValues = 	[mechPlate,bottomJaw,LeftSideJaw,RightSideJaw,jawServo,tSlotNutAssembly()]
	for (int i=0;i<returnValues.size();i++){
		int index = i
		returnValues[i] = returnValues[i]
		.setParameter(thickness)
		.setParameter(headDiameter)
		.setParameter(snoutLen)
		.setParameter(jawHeight)
		.setParameter(boltDiam)
		.setParameter(nutDiam)
		.setParameter(nutThick)
		.setRegenerate({ makeHead().get(index)})
	}
	return returnValues
}
CSGDatabase.clear()//set up the database to force only the default values in			
return makeHead();