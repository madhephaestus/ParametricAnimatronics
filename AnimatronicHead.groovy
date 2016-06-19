import eu.mihosoft.vrl.v3d.parametrics.*;
import com.neuronrobotics.bowlerstudio.vitamins.Vitamins;

/**
 * This script is used to make a parametric anamatronic creature head.
 * change the default values in LengthParameters to make changes perminant
 */
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
	LengthParameter upperHeadDiam 		= new LengthParameter("Upper Head Height",20,[300,0])
	LengthParameter leyeDiam 		= new LengthParameter("Left Eye Diameter",20,[headDiameter.getMM()/2,10])
	LengthParameter reyeDiam 		= new LengthParameter("Right Eye Diameter",20,[headDiameter.getMM()/2,10])
	LengthParameter eyeCenter 		= new LengthParameter("Eye Center Distance",headDiameter.getMM()/2,[headDiameter.getMM(),10])
	
	String jawServoName = "towerProMG91"
	
	double jawAttachOffset =  (headDiameter.getMM()/2
				-thickness.getMM()/2 
				-thickness.getMM())
     HashMap<String, Object> shaftmap = Vitamins.getConfiguration("hobbyServoHorn","standardMicro1")
	double hornOffset = shaftmap.get("hornThickness")
	HashMap<String,Object> jawServoConfig = Vitamins.getConfiguration("hobbyServo",jawServoName)
	double servoHeightFromMechPlate = Double.parseDouble(jawServoConfig.get("servoThinDimentionThickness"))/2
	double servoJawMountPlateOffset = Double.parseDouble(jawServoConfig.get("tipOfShaftToBottomOfFlange"))
	double servoWidth = Double.parseDouble(jawServoConfig.get("flangeLongDimention"))
	double servoCentering  = Double.parseDouble(jawServoConfig.get("shaftToShortSideFlandgeEdge"))
	double flangeMountOffset =  Double.parseDouble(jawServoConfig.get("tipOfShaftToBottomOfFlange"))
	double jawHingeSlotScale = 1.9
	double thicknessHoleRadius =  Math.sqrt(2*(thickness.getMM()/2)* (thickness.getMM()/2))
	CSG horn = Vitamins.get("hobbyServoHorn","standardMicro1")	
	CSG jawServo = Vitamins.get("hobbyServo",jawServoName)
                        .toZMax()
                        .roty(90)
                        .rotz(90)

                        
                        
                        
	CSG smallServo = Vitamins.get("hobbyServo","towerProMG91")

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
		new Cylinder(	headDiameter.getMM()/2 - thickness.getMM()*3,
					headDiameter.getMM()/2- thickness.getMM()*3,
					thickness.getMM(),(int)30).toCSG()
						.scalex(2*snoutLen.getMM()/headDiameter.getMM())
							,
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
	sideJaw=	tSlotPunch(sideJaw.rotz(90)).rotz(-90)


	horn=	horn
			.roty(90)
			.rotz(-90)
			.movey(-thickness.getMM()/2)
	def servoBrackets  =generateServoBracket(jawServoName)
			/*
	CSG []servoBrackets = new Cube(servoWidth+thickness.getMM()*3,
						thickness.getMM(),
						servoHeightFromMechPlate*2+thickness.getMM()
						).toCSG()
						.toZMin()
						.toYMax()
						.toXMax()
						.movex(servoCentering+thickness.getMM()*1.5)	
						.movey(-flangeMountOffset)
						.movez(-servoHeightFromMechPlate)
						*/
	
	def allJawServoParts = [horn,jawServo,servoBrackets.get(0),servoBrackets.get(1)].collect { 
		it.movez(	jawHeight.getMM() 
                       		 	+thickness.getMM()
                       		 	+servoHeightFromMechPlate
                        )
                        .movey(jawAttachOffset-thickness.getMM()/2+hornOffset/2)
					.setColor(javafx.scene.paint.Color.CYAN)
		} 
	//CSG servoBracket = jawServoParts[2].setColor(javafx.scene.paint.Color.WHITE)
	CSG LeftSideJaw =sideJaw
			.movey(jawAttachOffset) 
			.difference(
				allJawServoParts
			)
	
	CSG RightSideJaw =sideJaw
			.difference(new Cylinder(thicknessHoleRadius,thicknessHoleRadius,thickness.getMM()*2,(int)30).toCSG()
						.movez(-thickness.getMM())
						.rotx(-90)
						.movez(jawHeight.getMM()+thickness.getMM()+servoHeightFromMechPlate)
						)
			
			.movey(-jawAttachOffset) 
			
			
	def jawHingeParts =generateServoHinge(jawServoName).collect { 
							it.movez(	jawHeight.getMM() 
		                       		 	)
				                        .movey(-jawAttachOffset+thickness.getMM()/2)
									.setColor(javafx.scene.paint.Color.BLUE)
							}
	def upperHead = generateUpperHead()
	CSG leftEye = getEye(leyeDiam.getMM())
				.movey(eyeCenter.getMM()/2)
	CSG rightEye = getEye(reyeDiam.getMM())	
				.movey(-eyeCenter.getMM()/2)
			
	mechPlate = mechPlate
				.difference(LeftSideJaw.scalex(jawHingeSlotScale),RightSideJaw.scalex(jawHingeSlotScale))// scale forrro for the jaw to move
				.difference(allJawServoParts)
				.difference(jawHingeParts)
				.difference(upperHead)
	bottomJaw = bottomJaw.difference(
						LeftSideJaw,
						RightSideJaw,
						tSlotTabsWithHole()
							.rotz(90)
							.movey(jawAttachOffset), 
						tSlotTabsWithHole()
							.rotz(90)
							.movey(-jawAttachOffset) 	
						)
	
	CSG upperHeadPart = upperHead.get(0)					
	CSG jawServoBracket = allJawServoParts.get(2)
	CSG jawHingePin = jawHingeParts.get(0)
	
	upperHeadPart.setManufactuing({incoming ->
		return 	incoming
					
					.toZMin()
					.rotx(-90)
					.toZMin()
					.toYMax()
					.movey(- headDiameter.getMM()/2-1)
					
	})
	jawHingePin.setManufactuing({incoming ->
		return 	incoming
					.roty(90)
					.rotz (90)
					.toZMin()
					.toXMin()
					.movey(-jawHeight.getMM())
					
	})
	
	jawServoBracket.setManufactuing({incoming ->
		return 	incoming
					.rotx(90)
					.rotz (90)
					.toZMin()
					.toXMin()
					.movex(snoutLen.getMM()+JawSideWidth.getMM()+6)
					
	})
	
	RightSideJaw.setManufactuing({incoming ->
		return 	incoming
					.rotx(90)
					.toZMin()
					.toXMin()
					.movex(snoutLen.getMM()+1)
					
	})
	LeftSideJaw.setManufactuing({incoming ->
		return 	incoming
					.rotx(-90)
					.toZMin()
					.movey(-1)
					.toXMin()
					.movex(snoutLen.getMM()+1)
					
	})
	mechPlate.setManufactuing({incoming ->
		return 	incoming
					.toZMin()
					.movey(headDiameter.getMM())
	})
	
	
	def returnValues = 	[mechPlate,bottomJaw,RightSideJaw,LeftSideJaw,jawServoBracket,jawHingePin,upperHeadPart, leftEye,rightEye]
	def allParts = 	returnValues.collect { it.prepForManufacturing() } 
	CSG cutSheet = allParts.get(0).union(allParts)
	returnValues.add(cutSheet)
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
		.setParameter(upperHeadDiam)
		.setParameter(leyeDiam)
		.setParameter(reyeDiam)
		.setParameter(eyeCenter)
		.setRegenerate({ makeHead().get(index)})
	}
	
	return returnValues
}

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
			.union(new Cylinder(
				boltDiam.getMM()/2,
				boltDiam.getMM()/2,
				thickness.getMM(),
				(int)15).toCSG()
			.toZMin())
}

CSG tSlotKeepAway(){
	return tSlotTabs().hull()
}

CSG getEye(double diameter){
	CSG eye = new Sphere(diameter/2)// Spheres radius
				.toCSG()// convert to CSG to display
	return eye			
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

CSG tSlotPunch(CSG allignedIncoming){
	return allignedIncoming
			.difference(tSlotKeepAway(),tSlotNutAssembly())
			.union(tSlotTabs())
			
	
}

ArrayList <CSG> generateServoBracket(String servoName){
	LengthParameter boltDiam 		= new LengthParameter("Bolt Diameter",2.5,[8,2])
	LengthParameter thickness 		= new LengthParameter("Material Thickness",3.5,[10,1])
	HashMap<String,Object> jawServoConfig = Vitamins.getConfiguration("hobbyServo",servoName)
	double servoHeightFromMechPlate = Double.parseDouble(jawServoConfig.get("servoThinDimentionThickness"))/2
	double servoJawMountPlateOffset = Double.parseDouble(jawServoConfig.get("tipOfShaftToBottomOfFlange"))
	double servoWidth = Double.parseDouble(jawServoConfig.get("flangeLongDimention"))
	double servoCentering  = Double.parseDouble(jawServoConfig.get("shaftToShortSideFlandgeEdge"))
	double flangeMountOffset =  Double.parseDouble(jawServoConfig.get("tipOfShaftToBottomOfFlange"))
	double leftOffset = servoCentering+thickness.getMM()*1.5+boltDiam.getMM()
	double rightOffset = servoWidth-leftOffset+thickness.getMM()*2+boltDiam.getMM()
	
	CSG jawServo = Vitamins.get("hobbyServo",servoName)
                        .toZMax()
                        .roty(90)
                        .rotz(90)
     CSG bracket =  new Cube(servoWidth+thickness.getMM()*6+boltDiam.getMM()*4,
						thickness.getMM(),
						servoHeightFromMechPlate*2+thickness.getMM()
						).toCSG()
						.toZMin()
						.toXMax()
						.movex(servoCentering+thickness.getMM()*4+boltDiam.getMM()*2)	
						.movez(thickness.getMM())
	bracket=tSlotPunch(bracket
			.movex(rightOffset)
			.rotz(90)
			).rotz(-90).movex(-rightOffset)
	bracket=tSlotPunch(bracket
			.movex(-leftOffset)
			.rotz(90)
			).rotz(-90).movex(leftOffset)
	CSG bracketWithHoles = bracket
						.movex(-leftOffset)
						.rotz(90)
						.union( tSlotTabsWithHole())
						.rotz(-90)
						.movex(leftOffset)
	bracketWithHoles = bracketWithHoles
						.movex(rightOffset)
						.rotz(90)
						.union( tSlotTabsWithHole())
						.rotz(-90)
						.movex(-rightOffset)
	def bracketParts = [bracket,bracketWithHoles] .collect{
   		it.movez(-servoHeightFromMechPlate) 
			.toYMax()
			.movey(-flangeMountOffset)
			.movez(-thickness.getMM())
			.difference(jawServo)                   
   	}
	//bracketParts.add(jawServo)	            
   	return bracketParts
}

ArrayList <CSG> generateServoHinge(String servoName){
	LengthParameter boltDiam 		= new LengthParameter("Bolt Diameter",2.5,[8,2])
	LengthParameter thickness 		= new LengthParameter("Material Thickness",3.5,[10,1])
	HashMap<String,Object> jawServoConfig = Vitamins.getConfiguration("hobbyServo",servoName)
	double servoHeightFromMechPlate = Double.parseDouble(jawServoConfig.get("servoThinDimentionThickness"))/2
	double widthOfTab = thickness.getMM()*4+boltDiam.getMM()
	CSG pinAssembly = new Cube(	thickness.getMM(),
							widthOfTab,
							servoHeightFromMechPlate+thickness.getMM()
	
		).toCSG()
		.toZMin()
		.movez(thickness.getMM())
		
	pinAssembly =tSlotPunch(	pinAssembly)
				.toYMin()
				.union(new Cube(thickness.getMM()).toCSG()
						.movez(servoHeightFromMechPlate+thickness.getMM())
						.movey(-thickness.getMM()/2)
						)
	def parts = [pinAssembly,pinAssembly
						.union(tSlotTabsWithHole()
							.movey(widthOfTab/2)
						)]

	return parts
}

ArrayList <CSG> generateUpperHead(){
	LengthParameter thickness 		= new LengthParameter("Material Thickness",3.5,[10,1])
	LengthParameter headDiameter 		= new LengthParameter("Head Dimeter",100,[200,50])
	LengthParameter snoutLen 		= new LengthParameter("Snout Length",headDiameter.getMM(),[200,50])
	LengthParameter upperHeadDiam 		= new LengthParameter("Upper Head Height",20,[300,0])
	LengthParameter jawHeight 		= new LengthParameter("Jaw Height",50,[200,10])
	CSG upperHead = new Cylinder(	headDiameter.getMM()/2,
							headDiameter.getMM()/2,
							thickness.getMM(),
							(int)30).toCSG()
							.difference(new Cube(headDiameter.getMM()+snoutLen.getMM())
							.toCSG()
							.toYMin()
							
							)
	upperHead=upperHead
		.union( 
			upperHead.union(upperHead.movey(-upperHeadDiam.getMM())).hull(),
			upperHead
			.scalex(2*snoutLen.getMM()/headDiameter.getMM())
			.difference(new Cube(upperHeadDiam.getMM()+snoutLen.getMM())
							.toCSG()
							.toYMax()
							.toXMax()))
			.rotx(90)	
			.movey( -thickness.getMM()/2)	
			.movez(thickness.getMM())
	double moutOffset = headDiameter.getMM()/3
	upperHead = upperHead.rotz(90)
	upperHead = tSlotPunch(	upperHead				
	.movey(-moutOffset)
	)
	.movey(moutOffset)	
	upperHead = tSlotPunch(	upperHead				
	.movey(moutOffset)
	)
	.movey(-moutOffset)	

	upperHead = upperHead.rotz(-90)
	CSG 	upperHeadWithHoles = upperHead.union(tSlotTabsWithHole().rotz(90).movex(-moutOffset)	)
	.union(tSlotTabsWithHole().rotz(90).movex(moutOffset)	)
			
	def parts = [upperHead,upperHeadWithHoles].collect{
		it.movez(jawHeight.getMM())
	}

	return parts
}

//CSGDatabase.clear()//set up the database to force only the default values in	
//return  makeHead().collect { it.prepForManufacturing() } //generate the cuttable file
return makeHead()
