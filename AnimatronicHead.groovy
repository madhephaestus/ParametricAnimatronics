import eu.mihosoft.vrl.v3d.parametrics.*;
import com.neuronrobotics.bowlerstudio.vitamins.Vitamins;

/**
 * This script is used to make a parametric anamatronic creature head.
 * change the default values in LengthParameters to make changes perminant
 */
ArrayList<CSG> makeHead(){
	//Set up som parameters to use
	LengthParameter thickness 		= new LengthParameter("Material Thickness",3.15,[10,1])
	LengthParameter headDiameter 		= new LengthParameter("Head Dimeter",100,[200,50])
	LengthParameter snoutLen 		= new LengthParameter("Snout Length",57,[200,50])
	LengthParameter jawHeight 		= new LengthParameter("Jaw Height",50,[200,10])
	LengthParameter JawSideWidth 		= new LengthParameter("Jaw Side Width",20,[40,10])
	LengthParameter boltDiam 		= new LengthParameter("Bolt Diameter",4,[8,2])
	LengthParameter boltLength		= new LengthParameter("Bolt Length",10,[20,5])
	LengthParameter nutDiam 		= new LengthParameter("Nut Diameter",8.56,[10,3])
	LengthParameter nutThick 		= new LengthParameter("Nut Thickness",3,[10,3])
	LengthParameter upperHeadDiam 		= new LengthParameter("Upper Head Height",20,[300,0])
	LengthParameter leyeDiam 		= new LengthParameter("Left Eye Diameter",25,[headDiameter.getMM()/2,25])
	LengthParameter reyeDiam 		= new LengthParameter("Right Eye Diameter",headDiameter.getMM()/2-thickness.getMM()*4,[headDiameter.getMM()/2-thickness.getMM()*4,20])
	LengthParameter eyeCenter 		= new LengthParameter("Eye Center Distance",headDiameter.getMM()/2,[headDiameter.getMM(),10])
	LengthParameter ballJointPinSize 		= new LengthParameter("Ball Joint Ball Radius",8,[50,4])
	LengthParameter centerOfBall 		= new LengthParameter("Center Of Ball",18.5,[50,ballJointPinSize.getMM()])
	LengthParameter ballJointPin		= new LengthParameter("Ball Joint Pin Size",8,[50,ballJointPinSize.getMM()])
	LengthParameter eyemechRadius		= new LengthParameter("Eye Mech Linkage",10,[20,5])
	
	ArrayList<CSG> ballJointParts= (ArrayList<CSG>)ScriptingEngine.gitScriptRun(
                                "https://github.com/madhephaestus/cablePullServo.git", // git location of the library
	                              "ballJointBall.groovy" , // file to load
	                              null// no parameters (see next tutorial)
                        )
     CSG ballJoint = ballJointParts.get(0)
     CSG ballJointKeepAway = ballJointParts.get(1)
     
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

                        
                        
                        
	CSG smallServo = Vitamins.get("hobbyServo",jawServoName)

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
		
					
	BowlerStudioController.setCsg([bottomJaw]);
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
	//BowlerStudioController.addCsg((ArrayList<CSG>) allJawServoParts)
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
	//BowlerStudioController.addCsg((ArrayList<CSG>)jawHingeParts)
	def upperHead = generateUpperHead(mechPlate)
	/**
	 * Setting up the eyes
	 */
	double eyeHeight = jawHeight.getMM()+thickness.getMM()*2
	double minKeepaway =0;
	double bracketClearence = servoHeightFromMechPlate*2+thickness.getMM()
	if(leyeDiam.getMM()>reyeDiam.getMM()){
		minKeepaway=leyeDiam.getMM()/2
	}else
		minKeepaway=reyeDiam.getMM()/2
	
	if(	bracketClearence>	minKeepaway){
		minKeepaway=bracketClearence
	}
	eyeHeight+=minKeepaway
	double eyePlateHeight = eyeHeight - thickness.getMM()/2
	double eyeMechWeelPlateHeight = eyePlateHeight+smallServo.getMaxZ()+	thickness.getMM()
	double eyeStockBoltDistance = boltDiam.getMM()*2+	thickness.getMM()*4		
	
	eyeHeight +=ballJointPin.getMM()
	double eyeStockThickness = (ballJointPin.getMM()+4)/2
	
	double firstEyeBoltDistance = (Math.sqrt(Math.pow(headDiameter.getMM()/2,2)-Math.pow(eyeCenter.getMM()/2,2))
							-centerOfBall.getMM()
							+thickness.getMM()
							)
	double secondEyeBoltDistance = 	firstEyeBoltDistance- nutDiam.getMM()*2	
	double eyeLinkageLength = eyemechRadius.getMM()
	CSG bolt =new Cylinder(
						boltDiam.getMM()/2,
						boltDiam.getMM()/2,
						firstEyeBoltDistance*2,
						(int)15).toCSG()
						.movez(-firstEyeBoltDistance)		
	CSG bolts =	bolt
					.movex(firstEyeBoltDistance)
					.union(
						bolt
						.movex(secondEyeBoltDistance)	)	
		
	CSG eyeStockAttach = new Cube(headDiameter.getMM()/2
							-firstEyeBoltDistance
							-centerOfBall.getMM()
							+nutDiam.getMM()*2
							+thickness.getMM()*1.5
							,
							ballJointPin.getMM()+4,
							eyeStockThickness).toCSG()
						.toXMax()
						.movex(-centerOfBall.getMM()+thickness.getMM()/2)
						.toZMin()
	CSG eyestock = ballJoint
				.rotz(180)
				.union(eyeStockAttach)
				.rotx(180)
				.movex(headDiameter.getMM()/2)
				.difference(bolts)
				.movez(eyeHeight)
	CSG leftEye = getEye(leyeDiam.getMM(),ballJointKeepAway)
				.movey(eyeCenter.getMM()/2)
				.movex(headDiameter.getMM()/2)
				.movez(eyeHeight)
	CSG rightEye = getEye(reyeDiam.getMM(),ballJointKeepAway)	
				.movey(-eyeCenter.getMM()/2)
				.movex(headDiameter.getMM()/2)
				.movez(eyeHeight)
	BowlerStudioController.addCsg(leftEye)
	BowlerStudioController.addCsg(rightEye)
	CSG eyeKeepAwayr =new Sphere(reyeDiam.getMM()/2+1)// Spheres radius
					.toCSG()
				.movey(-eyeCenter.getMM()/2)		
				.movex(headDiameter.getMM()/2)
				.movez(eyePlateHeight-thickness.getMM())
	eyeKeepAwayr= eyeKeepAwayr.union(eyeKeepAwayr.movez(	thickness.getMM()*2)).hull()

	CSG eyeKeepAwayl =new Sphere(leyeDiam.getMM()/2+1)// Spheres radius
					.toCSG()
				.movey(eyeCenter.getMM()/2)		
				.movex(headDiameter.getMM()/2)
				.movez(eyePlateHeight-thickness.getMM())
	eyeKeepAwayl= eyeKeepAwayl.union(eyeKeepAwayl.movez(	thickness.getMM()*2)).hull()

	CSG eyeKeepAway = eyeKeepAwayl.union(eyeKeepAwayr)
					.difference(new Cube(headDiameter.getMM())
					.toCSG()
					.toXMax()
					.movex(firstEyeBoltDistance+boltDiam.getMM())
					.movez(eyePlateHeight-thickness.getMM())
					)
		
	CSG leftBallJoint =  eyestock.movey(  eyeCenter.getMM()/2)
	CSG rightBallJoint = eyestock.movey( -eyeCenter.getMM()/2)
	BowlerStudioController.addCsg(leftBallJoint)
	BowlerStudioController.addCsg(rightBallJoint)
	CSG upperHeadPart = upperHead.get(0)
	CSG eyePlate=baseHead
				.movez(eyePlateHeight)
	CSG eyeMechWheel= new Cylinder(
						eyeLinkageLength,
						eyeLinkageLength,
						thickness.getMM(),
						(int)15).toCSG().difference(bolt)
						.movez(eyeMechWeelPlateHeight)
	CSG mechLinkage = new Cylinder(boltDiam.getMM(),
						boltDiam.getMM(),
						thickness.getMM(),
						(int)15).toCSG()
	mechLinkage =mechLinkage
		.movey(eyeCenter.getMM()/2)
		.union(mechLinkage.movey(-eyeCenter.getMM()/2))
		.hull()
		.difference(bolt.movey(eyeCenter.getMM()/2))
		.difference(bolt.movey(-eyeCenter.getMM()/2))
		.movez(eyeMechWeelPlateHeight+thickness.getMM())
		.movex(-eyeLinkageLength+boltDiam.getMM())
	CSG mechLinkage2 = mechLinkage.movex(eyeLinkageLength)
	mechLinkage=mechLinkage.movex(-eyeLinkageLength)
					.movey(eyeLinkageLength)
					.union(mechLinkage2)
	BowlerStudioController.addCsg(mechLinkage)
	for(int i=0;i<4;i++){
		eyeMechWheel=eyeMechWheel
					.difference(
						bolt.movez(eyeMechWeelPlateHeight)
							.movex(eyeLinkageLength-boltDiam.getMM())
							.rotz(90*i)
							)
	}
	CSG eyeMechWheel1 = eyeMechWheel
						.movey(-eyeCenter.getMM()/2)
						.movex(eyeLinkageLength)	
	CSG eyeMechWheel2 = eyeMechWheel
						.movey(-eyeCenter.getMM()/2+eyeLinkageLength)
						.movex(-eyeLinkageLength)
	CSG eyeMechWheel3 = eyeMechWheel
						.movey(eyeCenter.getMM()/2+eyeLinkageLength)
						.movex(-eyeLinkageLength)
	CSG eyeMechWheel4 = eyeMechWheel
						.movey(eyeCenter.getMM()/2)
						.movex(eyeLinkageLength)	
	CSG eyeBoltPan1 =bolt.movez(eyePlateHeight)
						.movey(eyeCenter.getMM()/2+eyeLinkageLength)
						.movex(-eyeLinkageLength)
	CSG eyeBoltPan2 =bolt.movez(eyePlateHeight)
						.movey(eyeCenter.getMM()/2)
						.movex(eyeLinkageLength)
	eyeMechWheel = eyeMechWheel1.union(eyeMechWheel2,eyeMechWheel3,eyeMechWheel4)	
	BowlerStudioController.addCsg(eyeMechWheel)							
	// CUt the slot for the eye mec from the upper head
	CSG mechKeepaway=mechLinkage.movex(-thickness.getMM()).union(mechLinkage.movex(eyeLinkageLength)).hull()	
					.movez(thickness.getMM())
	mechKeepaway=mechKeepaway.union(mechKeepaway.movez(-thickness.getMM()*2)).hull()				
			
	upperHeadPart = upperHeadPart
				.difference(eyePlate
				.movex(-headDiameter.getMM()/2))
				.difference(mechKeepaway)
	BowlerStudioController.addCsg(upperHeadPart)		
	CSG eyePan = smallServo
				.movez(eyePlateHeight+thickness.getMM())
				.movey(-eyeCenter.getMM()/2)
				.movex(eyeLinkageLength)
	CSG eyeTilt = smallServo.clone()
				.movez(eyePlateHeight+thickness.getMM())
				.movey(-eyeCenter.getMM()/2+eyeLinkageLength)
				.movex(-eyeLinkageLength)
				
	//cut a matching slot from the eye plate 					
	eyePlate = eyePlate	.difference(upperHeadPart)
					.difference(bolts.movey(-eyeCenter.getMM()/2).movez(eyeHeight))
					.difference(bolts.movey(eyeCenter.getMM()/2).movez(eyeHeight))
					.difference(eyePan,eyeTilt,eyeBoltPan1,eyeBoltPan2,eyeKeepAway)
	BowlerStudioController.addCsg(eyePlate)	
	mechPlate = mechPlate
				.difference(LeftSideJaw.scalex(jawHingeSlotScale),RightSideJaw.scalex(jawHingeSlotScale))// scale forrro for the jaw to move
				.difference(allJawServoParts)
				.difference(jawHingeParts)
				.difference(upperHead)
	BowlerStudioController.addCsg(mechPlate)	
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
	
						
	CSG jawServoBracket = allJawServoParts.get(2)
	CSG jawHingePin = jawHingeParts.get(0)
	eyePlate.setColor(javafx.scene.paint.Color.WHITE)
	rightEye.setManufactuing({incoming ->
		return 	incoming.roty(90)
					.toZMin()
					.toXMin()
					.toYMin()
					.movey(headDiameter.getMM())
					.movex( -headDiameter.getMM())
					
	})
	leftEye.setManufactuing({incoming ->
		return 	incoming.roty(90)
					.toZMin()
					.toXMin()
					.toYMin()
					.movey(-headDiameter.getMM()*1.3)
					.movex( headDiameter.getMM()*2/3)
					
	})
	rightBallJoint.setManufactuing({incoming ->
		return 	incoming.roty(180)
					.toZMin()
					.toXMin()
					.toYMin()
					.movey(headDiameter.getMM()-reyeDiam.getMM())
					.movex( -headDiameter.getMM())
					
	})
	leftBallJoint.setManufactuing({incoming ->
		return 	incoming.roty(180)
					.toZMin()
					.toXMin()
					.toYMin()
					.movey(headDiameter.getMM()-reyeDiam.getMM() +1+ballJointPinSize.getMM()*2 )
					
					.movex( -headDiameter.getMM())
					
	})
	eyeMechWheel.setManufactuing({incoming ->
		return 	incoming.toZMin()
					.toXMax()
					.movex( -headDiameter.getMM()+eyeLinkageLength)
					.movey(- headDiameter.getMM())
					
	})
	mechLinkage.setManufactuing({incoming ->
		return 	incoming.toZMin()
					.toXMax()
					.movex( -headDiameter.getMM()+eyeLinkageLength*4)
					.movey(- headDiameter.getMM())
					
	})
	eyePlate.setManufactuing({incoming ->
		return 	incoming.toZMin()
					.toXMax()
					.movex( -headDiameter.getMM()/5)
					
	})
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
	
	
	def returnValues = 	[
					mechPlate,
					bottomJaw,
					RightSideJaw,
					LeftSideJaw,
					jawServoBracket,
					jawHingePin,
					upperHeadPart, 
					leftEye,
					rightEye,
					leftBallJoint,
					rightBallJoint,
					eyePlate,
					//eyePan,
					//eyeTilt,
					eyeMechWheel,
					mechLinkage
					]
	print "\nBuilding cut sheet..."
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
		.setParameter(ballJointPinSize)
		.setParameter(centerOfBall)
		.setParameter(ballJointPinSize)
		.setParameter(boltLength)
		.setRegenerate({ makeHead().get(index)})
		
		
	}
	BowlerStudioController.setCsg(returnValues)	
	print "Done!\n"
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

CSG getEye(double diameter,CSG ballJointKeepAway){
	LengthParameter eyemechRadius		= new LengthParameter("Eye Mech Linkage",10,[20,5])
	LengthParameter boltDiam 		= new LengthParameter("Bolt Diameter",2.5,[8,2])
	LengthParameter thickness 		= new LengthParameter("Material Thickness",3.5,[10,1])
	ballJointKeepAway= ballJointKeepAway
					.union(
						ballJointKeepAway
						.union(ballJointKeepAway.movex(-10))
						.hull()
						.difference(new Cube(diameter)
						.toCSG()
						.toZMin()
						)
						)
	CSG eye = new Sphere(diameter/2)// Spheres radius
				.toCSG()// convert to CSG to display
				.difference(new Cube(diameter).toCSG().toXMax().movex(-3))
				.difference(ballJointKeepAway)
	CSG slot = new Cylinder(
				boltDiam.getMM(),
				boltDiam.getMM(),
				thickness.getMM(),
				(int)15).toCSG()
				.difference(new Cylinder(
				boltDiam.getMM()/2,
				boltDiam.getMM()/2,
				thickness.getMM(),
				(int)15).toCSG())
				.movez(-thickness.getMM()/2)
				.roty(90)
				.rotz(90)
				.toXMax()
				
	for (int i=0;i<4;i++){
		eye=eye.difference(slot.movez(eyemechRadius.getMM()+boltDiam.getMM()/2).rotx(90*i))
	}
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

ArrayList <CSG> generateUpperHead(CSG lowerHead){
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
	double moutOffset = lowerHead.getMinX()+thickness.getMM()*3
	upperHead = upperHead.rotz(90)
	upperHead = tSlotPunch(	upperHead				
	.movey(moutOffset)
	)
	.movey(-moutOffset)	

	moutOffset = lowerHead.getMaxX()-thickness.getMM()*3
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

CSGDatabase.clear()//set up the database to force only the default values in	
//return  makeHead().collect { it.prepForManufacturing() } //generate the cuttable file
return makeHead()
