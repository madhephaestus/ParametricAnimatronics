import eu.mihosoft.vrl.v3d.parametrics.*;
import com.neuronrobotics.bowlerstudio.vitamins.Vitamins;

/**
 * This script is used to make a parametric anamatronic creature head.
 * change the default values in LengthParameters to make changes perminant
 */
ArrayList<CSG> makeHead(){
	//Set up som parameters to use
	LengthParameter thickness 		= new LengthParameter("Material Thickness",3.15,[10,1])
	LengthParameter headDiameter 		= new LengthParameter("Head Dimeter",150,[200,50])
	LengthParameter snoutLen 		= new LengthParameter("Snout Length",85,[200,50])
	LengthParameter jawHeight 		= new LengthParameter("Jaw Height",32,[200,10])
	LengthParameter JawSideWidth 		= new LengthParameter("Jaw Side Width",20,[40,10])
	LengthParameter boltDiam 		= new LengthParameter("Bolt Diameter",3.0,[8,2])
	LengthParameter boltLength		= new LengthParameter("Bolt Length",10,[18,10])
	LengthParameter nutDiam 		 	= new LengthParameter("Nut Diameter",5.42,[10,3])
	LengthParameter nutThick 		= new LengthParameter("Nut Thickness",2.4,[10,3])
	LengthParameter upperHeadDiam 	= new LengthParameter("Upper Head Height",20,[300,0])
	LengthParameter leyeDiam 		= new LengthParameter("Left Eye Diameter",35,[headDiameter.getMM()/2,29])
	LengthParameter reyeDiam 		= new LengthParameter("Right Eye Diameter",35,[headDiameter.getMM()/2-thickness.getMM()*4,29])
	LengthParameter eyeCenter 		= new LengthParameter("Eye Center Distance",headDiameter.getMM()/2,[headDiameter.getMM(),10])
	LengthParameter ballJointPin		= new LengthParameter("Ball Joint Pin Size",8,[50,8])
	LengthParameter centerOfBall 		= new LengthParameter("Center Of Ball",18.5,[50,8])
	LengthParameter printerOffset		= new LengthParameter("printerOffset",0.5,[2,0.001])
	LengthParameter eyemechRadius		= new LengthParameter("Eye Mech Linkage",10,[20,5])
	LengthParameter eyemechWheelHoleDiam	= new LengthParameter("Eye Mech Wheel Center Hole Diam",7.25,[8,3])
	LengthParameter wireDiam			= new LengthParameter("Connection Wire Diameter",1.6,[boltDiam.getMM(),1])

	
	ballJointPin.setMM(4)
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
				-thickness.getMM()*2)
     HashMap<String, Object> shaftmap = Vitamins.getConfiguration("hobbyServoHorn","standardMicro1")
	double hornOffset = shaftmap.get("hornThickness")
	HashMap<String,Object> jawServoConfig = Vitamins.getConfiguration("hobbyServo",jawServoName)
	double servoHeightFromMechPlate = Double.parseDouble(jawServoConfig.get("servoThinDimentionThickness"))/2
	double servoJawMountPlateOffset = Double.parseDouble(jawServoConfig.get("tipOfShaftToBottomOfFlange"))
	double servoWidth = Double.parseDouble(jawServoConfig.get("flangeLongDimention"))
	double servoCentering  = Double.parseDouble(jawServoConfig.get("shaftToShortSideFlandgeEdge"))
	double flangeMountOffset =  Double.parseDouble(jawServoConfig.get("tipOfShaftToBottomOfFlange"))
	double flangeThickness =  Double.parseDouble(jawServoConfig.get("flangeThickness"))
	double servoShaftSideHeight =  Double.parseDouble(jawServoConfig.get("servoShaftSideHeight"))	
	double bottomOfFlangeToTopOfBody =  Double.parseDouble(jawServoConfig.get("bottomOfFlangeToTopOfBody"))
	double jawHingeSlotScale = 1.9
	double thicknessHoleRadius =  Math.sqrt(2*(thickness.getMM()/2)* (thickness.getMM()/2))
	double servoLongSideOffset = servoWidth-servoCentering
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
		new Cylinder(	headDiameter.getMM()/2 - thickness.getMM()*4,
					headDiameter.getMM()/2- thickness.getMM()*4,
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
	
	def allJawServoParts = [horn,jawServo,servoBrackets.get(0),servoBrackets.get(1)].collect { 
		it.movez(	jawHeight.getMM() 
                       		 	+thickness.getMM()
                       		 	+servoHeightFromMechPlate
                        )
                        .movey(jawAttachOffset-thickness.getMM()/2+hornOffset/2)
					.setColor(javafx.scene.paint.Color.CYAN)
		} 

	CSG LeftSideJaw =sideJaw
			.movey(jawAttachOffset) 
			.difference(
				allJawServoParts
			)
	.setColor(javafx.scene.paint.Color.CYAN)
	CSG RightSideJaw =sideJaw
			.difference(new Cylinder(thicknessHoleRadius,thicknessHoleRadius,thickness.getMM()*2,(int)30).toCSG()
						.movez(-thickness.getMM())
						.rotx(-90)
						.movez(jawHeight.getMM()+thickness.getMM()+servoHeightFromMechPlate)
						)
			
			.movey(-jawAttachOffset) 
			.setColor(javafx.scene.paint.Color.CYAN)
	BowlerStudioController.addCsg(LeftSideJaw);		
	BowlerStudioController.addCsg(RightSideJaw);	
	def upperHead = generateUpperHead(mechPlate)
	/**
	 * Setting up the eyes
	 */
	double eyeHeight = jawHeight.getMM()+thickness.getMM()
	double minKeepaway =0;
	//double flangeThickness =  Double.parseDouble(jawServoConfig.get("flangeThickness"))
	//double servoShaftSideHeight =  Double.parseDouble(jawServoConfig.get("servoShaftSideHeight"))	
	//double bottomOfFlangeToTopOfBody =  Double.parseDouble(jawServoConfig.get("bottomOfFlangeToTopOfBody"))
	double bracketClearence = servoShaftSideHeight-bottomOfFlangeToTopOfBody+flangeThickness*2+thickness.getMM()
	if(leyeDiam.getMM()>reyeDiam.getMM()){
		minKeepaway=leyeDiam.getMM()/2
	}else
		minKeepaway=reyeDiam.getMM()/2
	
	if(	bracketClearence>	minKeepaway){
		minKeepaway=bracketClearence
	}
	if(boltLength.getMM()*2+thickness.getMM()>minKeepaway){
		minKeepaway = boltLength.getMM()*2+thickness.getMM()
	}
	eyeHeight+=minKeepaway
	double eyePlateHeight = eyeHeight - thickness.getMM()/2

	double eyeStockBoltDistance = boltDiam.getMM()*2+	thickness.getMM()*4		
	double eyestockStandoffDistance= bottomOfFlangeToTopOfBody+thickness.getMM()/2
	eyeHeight +=eyestockStandoffDistance
	double eyeStockThickness = ballJointPin.getMM()
	
	double firstEyeBoltDistance = (Math.sqrt(Math.pow(headDiameter.getMM()/2,2)-Math.pow(eyeCenter.getMM()/2,2))
							-centerOfBall.getMM()
							+eyemechRadius.getMM()
							)
	
	double eyeLinkageLength = eyemechRadius.getMM()
	double titlServoPlacement = -(eyeLinkageLength+boltDiam.getMM()*2)
	double panServoPlacement  = (eyeLinkageLength+boltDiam.getMM()*2)
	double tiltWheelheight = eyePlateHeight+smallServo.getMaxZ()+	thickness.getMM()
	double panWheelheight = eyePlateHeight+smallServo.getMaxZ()-	flangeThickness - thickness.getMM()
	double eyeXdistance  =headDiameter.getMM()/2
	double eyeBoltDistance =eyeCenter.getMM()/2-servoLongSideOffset+thickness.getMM()
	CSG bolt =new Cylinder(
						boltDiam.getMM()/2,
						boltDiam.getMM()/2,
						firstEyeBoltDistance*2,
						(int)15).toCSG()
						.movez(-firstEyeBoltDistance)	
	CSG printedBolt =new Cylinder(
						(boltDiam.getMM()+printerOffset.getMM())/2,
						(boltDiam.getMM()+printerOffset.getMM())/2,
						firstEyeBoltDistance*2,
						(int)15).toCSG()
						.movez(-firstEyeBoltDistance)	
						
	CSG wire = new Cylinder(wireDiam.getMM()/2,
						wireDiam.getMM()/2
						,firstEyeBoltDistance*2,(int)15).toCSG()
						.movez(-firstEyeBoltDistance)		
	CSG bolts =	bolt.union(
						bolt
						.movey(-nutDiam.getMM()	)	)
	CSG printedBolts =	printedBolt.union(
						printedBolt
						.movey(-nutDiam.getMM()	)	)				
						
		
	CSG eyeStockAttach = new Cube(headDiameter.getMM()/2
							-firstEyeBoltDistance
							-centerOfBall.getMM()
							+nutDiam.getMM()*2
							,
							nutDiam.getMM()*2.5,
							eyestockStandoffDistance-thickness.getMM()/2).toCSG()
						.toXMax()
						.movex(-centerOfBall.getMM()+nutDiam.getMM()/4)
						.toZMin()
						.movex(boltDiam.getMM()*2)
	CSG eyeStockanchor = new Cube(thickness.getMM(),
							ballJointPin.getMM()*2,
							eyeStockThickness).toCSG()
						.toXMax()
						.movex(-centerOfBall.getMM()+thickness.getMM()/2)
						.toZMin()
	double eyeStockMountLocation = (eyeCenter.getMM()/2)-eyeBoltDistance+boltDiam.getMM()*1.5
	CSG rigtStockAttach = eyeStockanchor
						.union(
							eyeStockAttach
							.toYMin()
							.movey(-eyeStockMountLocation)
							)
						.hull()
	CSG leftStockAttach = eyeStockanchor
						.union(
							eyeStockAttach
							.toYMax()
							.movey(eyeStockMountLocation)
							)
						.hull()				
	CSG eyestockRight = ballJoint
				.rotz(180)
				.union(rigtStockAttach)
				.rotx(180)
				.movex(headDiameter.getMM()/2)
				.difference(printedBolts
						.movex(firstEyeBoltDistance)
						.movey(eyeCenter.getMM()/2-eyeBoltDistance))
				.movez(eyeHeight)
	CSG eyestockLeft = ballJoint
				.rotz(180)
				.union(leftStockAttach)
				.rotx(180)
				.movex(headDiameter.getMM()/2)
				.difference(
						printedBolts
						.rotz(180)
						.movex(firstEyeBoltDistance)
						.movey(-eyeCenter.getMM()/2+eyeBoltDistance))
				.movez(eyeHeight)
	

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
		
	CSG leftBallJoint =  eyestockLeft.movey(  eyeCenter.getMM()/2)
					.setColor(javafx.scene.paint.Color.BLUE)
	CSG rightBallJoint = eyestockRight.movey( -eyeCenter.getMM()/2)
					.setColor(javafx.scene.paint.Color.BLUE)
	BowlerStudioController.addCsg(leftBallJoint)
	BowlerStudioController.addCsg(rightBallJoint)
	CSG upperHeadPart = upperHead.get(0)
	CSG eyePlate=baseHead
				.movez(eyePlateHeight)
				eyemechWheelHoleDiam
	CSG eyeMechWheel= new Cylinder(
						eyeLinkageLength+boltDiam.getMM(),
						eyeLinkageLength+boltDiam.getMM(),
						thickness.getMM(),
						(int)15).toCSG()
	//Generate Linkages					
	CSG mechLinkageCore = new Cylinder(boltDiam.getMM(),
						boltDiam.getMM(),
						thickness.getMM(),
						(int)15).toCSG()
	CSG mechLinkageAttach = new Cylinder(boltDiam.getMM()*1.5,
						boltDiam.getMM()*1.5,
						thickness.getMM(),
						(int)15).toCSG()
	CSG mechLinkage =mechLinkageCore
		.movey(eyeCenter.getMM()/2)
		.union(mechLinkageCore.movey(-eyeCenter.getMM()/2))
		.hull()
		.union(mechLinkageAttach.movey(eyeCenter.getMM()/2))
		.union(mechLinkageAttach.movey(-eyeCenter.getMM()/2))
		.difference(bolt.movey(eyeCenter.getMM()/2))
		.difference(bolt.movey(-eyeCenter.getMM()/2))

	
		
	CSG mechLinkage2 = mechLinkage
					.movex(panServoPlacement-eyeLinkageLength)
					.movez(panWheelheight+thickness.getMM())
	mechLinkage=mechLinkage
					.movex(titlServoPlacement-eyeLinkageLength)
					.movey(eyeLinkageLength)
					.movez(tiltWheelheight+thickness.getMM())
					//.union(mechLinkage2)
	//keepaway for the linkages				
	CSG mechKeepaway=	mechLinkage
					.union(mechLinkage2)
					.movex(-eyeLinkageLength+boltDiam.getMM())
					.union(mechLinkage
						.movex(eyeLinkageLength))
					.hull()	
					.movez(thickness.getMM())
					mechKeepaway=mechKeepaway
								.union(mechKeepaway
										.movez(-thickness.getMM()*2))
										
								.hull()
	mechKeepaway = mechKeepaway
				.union(mechKeepaway.movex(thickness.getMM()+eyeLinkageLength))
				.union(mechKeepaway.movex(-headDiameter.getMM()))
				.hull()	
	//Eye to wheel linkage
	double tiltLinkagelength = -titlServoPlacement +eyeXdistance - boltDiam.getMM()*2
	double panLinkagelength = -panServoPlacement +eyeXdistance - boltDiam.getMM()*2
	
	CSG tiltEyeLinkage  = 	mechLinkageCore
		.union(mechLinkageCore.movex(tiltLinkagelength))
		.hull()
		.union(mechLinkageAttach)
		.difference(bolt)
		.difference(wire.movex(tiltLinkagelength))
		.movez(tiltWheelheight+thickness.getMM())
		.movex(titlServoPlacement)
		.movey(-eyeCenter.getMM()/2)
	CSG tiltEyeLinkage2 = tiltEyeLinkage
		.movey(eyeCenter.getMM())

	CSG panEyeLinkage  = 	mechLinkageCore
		.union(mechLinkageCore.movex(panLinkagelength))
		.hull()
		.union(mechLinkageAttach)
		.difference(bolt)
		.difference(wire.movex(panLinkagelength))
		.movez(panWheelheight+thickness.getMM())
		.movex(panServoPlacement)
		.movey(-eyeCenter.getMM()/2-eyeLinkageLength)
	CSG panEyeLinkage2 = panEyeLinkage
		.movey(eyeCenter.getMM()+eyeLinkageLength*2)
		
	BowlerStudioController.addCsg(mechLinkage)
	BowlerStudioController.addCsg(mechLinkage2)
	BowlerStudioController.addCsg(tiltEyeLinkage)
	BowlerStudioController.addCsg(tiltEyeLinkage2)		
	BowlerStudioController.addCsg(panEyeLinkage)
	BowlerStudioController.addCsg(panEyeLinkage2)
	CSG slot = wire.union(wire.movex(eyeLinkageLength/3)).hull()
	// Make the linkage wheels
	for(int i=0;i<4;i++){
		eyeMechWheel=eyeMechWheel
					.difference(
						bolt
							.movex(eyeLinkageLength)
							.rotz(90*i)
							)
		if(i%2==0){
			eyeMechWheel=eyeMechWheel
					.difference(
						slot
							.movex(eyeLinkageLength/2)
							.rotz(90*i-45)
							)
		}
	}
	CSG eyeMechWheel1 = eyeMechWheel
						.difference(
							new Cylinder(
								eyemechWheelHoleDiam.getMM()/2,
								eyemechWheelHoleDiam.getMM()/2,
								thickness.getMM(),
								(int)15).toCSG())
						.movey(-eyeCenter.getMM()/2)
						.movex(panServoPlacement)	
	CSG eyeMechWheel2 = eyeMechWheel
						.difference(
							new Cylinder(
								eyemechWheelHoleDiam.getMM()/2,
								eyemechWheelHoleDiam.getMM()/2,
								thickness.getMM(),
								(int)15).toCSG()
								)
						.movey(-eyeCenter.getMM()/2+eyeLinkageLength)
						.movex(titlServoPlacement)
	CSG eyeMechWheel3 = eyeMechWheel
						.difference(bolt)
						.movey(eyeCenter.getMM()/2+eyeLinkageLength)
						.movex(titlServoPlacement)
	CSG eyeMechWheel4 = eyeMechWheel
						.difference(bolt)
						.movey(eyeCenter.getMM()/2)
						.movex(panServoPlacement)	
	CSG eyeBoltPan1 =bolt.movez(eyePlateHeight)
						.movey(eyeCenter.getMM()/2+eyeLinkageLength)
						.movex(titlServoPlacement)
	CSG eyeBoltPan2 =bolt.movez(eyePlateHeight)
						.movey(eyeCenter.getMM()/2)
						.movex(panServoPlacement)
	
	eyeMechWheelPan = eyeMechWheel1.union(eyeMechWheel4)	
				.movez(panWheelheight)
	eyeMechWheelTilt = eyeMechWheel2.union(eyeMechWheel3)	
				.movez(tiltWheelheight)
				
	BowlerStudioController.addCsg(eyeMechWheelPan)		
	BowlerStudioController.addCsg(eyeMechWheelTilt)							
	// Cut the slot for the eye mec from the upper head
	upperHeadPart = upperHeadPart
				.difference(eyePlate
				.movex(-headDiameter.getMM()/2))
				.difference(mechKeepaway)
	BowlerStudioController.addCsg(upperHeadPart)		
	CSG eyePan = smallServo
				.rotz(180)
				.movez(eyePlateHeight-flangeThickness)
				.movey(-eyeCenter.getMM()/2)
				.movex(panServoPlacement)
	BowlerStudioController.addCsg(eyePan)
	CSG eyeTilt = smallServo.clone()
				.movez(eyePlateHeight+thickness.getMM())
				.movey(-eyeCenter.getMM()/2+eyeLinkageLength)
				.movex(titlServoPlacement)
	//BowlerStudioController.addCsg(eyeTilt)
	def jawHingeParts =generateServoHinge(jawServoName,eyePlateHeight-jawHeight.getMM()).collect { 
							it.movez(	jawHeight.getMM() 
		                       		 	)
				                        .movey(-jawAttachOffset+thickness.getMM()/2)
									.setColor(javafx.scene.paint.Color.BLUE)
							}
	/**			
	 * 			Building the main plates
	 * 			
	 */
	//cut a matching slot from the eye plate 					
	eyePlate = eyePlate	.difference(upperHeadPart,upperHeadPart.movex(10))
					.difference(bolts
								.movex(firstEyeBoltDistance)
								.movey(-eyeBoltDistance)
								.movez(eyeHeight))
					.difference(bolts
								.rotz(180)
								.movex(firstEyeBoltDistance)
								.movey(eyeBoltDistance)
								.movez(eyeHeight))
					.difference(eyePan,eyeTilt,eyeBoltPan1,eyeBoltPan2,eyeKeepAway)
					.difference(jawHingeParts)
	BowlerStudioController.addCsg(eyePlate)	
	mechPlate = mechPlate
				.difference(LeftSideJaw.scalex(jawHingeSlotScale),RightSideJaw.scalex(jawHingeSlotScale))// scale forrro for the jaw to move
				.difference(allJawServoParts)
				.difference(jawHingeParts)
				.difference(upperHead)
				.difference(eyePan,eyeTilt)
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
	
		print "\nLoading eyes..."
	CSG leftEye = getEye(leyeDiam.getMM(),ballJointKeepAway)
				.movey(eyeCenter.getMM()/2)
				.movex(eyeXdistance)
				.movez(eyeHeight)
				
	CSG rightEye = getEye(reyeDiam.getMM(),ballJointKeepAway)	
				.movey(-eyeCenter.getMM()/2)
				.movex(eyeXdistance)
				.movez(eyeHeight)
	print "Done\n"			
	BowlerStudioController.addCsg(leftEye)
	BowlerStudioController.addCsg(rightEye)					
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
					.movey(headDiameter.getMM()-reyeDiam.getMM() +1+16 )
					
					.movex( -headDiameter.getMM())
					
	})
	eyeMechWheelPan.setManufactuing({incoming ->
		return 	incoming.toZMin()
					.toXMax()
					.movex( -headDiameter.getMM()+eyeLinkageLength)
					.movey(- headDiameter.getMM())
					
	})
	eyeMechWheelTilt.setManufactuing({incoming ->
		return 	incoming.toZMin()
					.toXMin()
					.movex( -headDiameter.getMM()+eyeLinkageLength+1)
					.movey(- headDiameter.getMM())
					
	})
	mechLinkage.setManufactuing({incoming ->
		return 	incoming.toZMin()
					.toXMin()
					.toYMin()
					.rotz(90)
					.movey(-headDiameter.getMM()-upperHeadDiam.getMM()-boltDiam.getMM()*2 )
					
					
	})
	mechLinkage2.setManufactuing({incoming ->
		return 	incoming.toZMin()
				.toXMin()
					.toYMin()
					.rotz(90)
					.movey(-headDiameter.getMM()-upperHeadDiam.getMM()-boltDiam.getMM()*5 )	
					
	})
	tiltEyeLinkage.setManufactuing({incoming ->
		return 	incoming.toZMin()
				.toXMin()
					.toYMin()
					.movey(-headDiameter.getMM()-upperHeadDiam.getMM()-boltDiam.getMM()*10 )	
					
	})
	tiltEyeLinkage2.setManufactuing({incoming ->
		return 	incoming.toZMin()
				.toXMin()
					.toYMin()
					.movey(-headDiameter.getMM()-upperHeadDiam.getMM()-boltDiam.getMM()*13 )	
					
	})
	panEyeLinkage.setManufactuing({incoming ->
		return 	incoming.toZMin()
				.toXMin()
					.toYMin()
					.movey(-headDiameter.getMM()-upperHeadDiam.getMM()-boltDiam.getMM()*16 )	
					
	})
	panEyeLinkage2.setManufactuing({incoming ->
		return 	incoming.toZMin()
				.toXMin()
					.toYMin()
					.movey(-headDiameter.getMM()-upperHeadDiam.getMM()-boltDiam.getMM()*19 )	
					
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
					eyeMechWheelTilt,eyeMechWheelPan,
					mechLinkage,mechLinkage2,
					tiltEyeLinkage,tiltEyeLinkage2,
					panEyeLinkage,panEyeLinkage2
					]
	print "\nBuilding cut sheet... "
	//def allParts = 	returnValues.collect { it.prepForManufacturing() } 
	//CSG cutSheet = allParts.get(0).union(allParts)
	//returnValues.add(cutSheet)
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
		.setParameter(printerOffset)
		//.setParameter(ballJointPinSize)
		//.setParameter(centerOfBall)
		//.setParameter(ballJointPinSize)
		.setParameter(boltLength)
		//.setParameter(eyemechRadius)
		.setParameter(eyemechWheelHoleDiam)
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
	CSG eye = new Sphere(diameter/2,40,20)// Spheres radius
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
		eye=eye
		.difference(
			slot
			.rotx(-90*i)
			.movez(
				eyemechRadius.getMM()+boltDiam.getMM()/2)
				.rotx(90*i))
	}
	return eye			
}

CSG tSlotNutAssembly(){
	LengthParameter boltDiam 		= new LengthParameter("Bolt Diameter",2.5,[8,2])
	LengthParameter nutDiam 		= new LengthParameter("Nut Diameter",4,[10,3])
	LengthParameter thickness 		= new LengthParameter("Material Thickness",3.5,[10,1])
	LengthParameter nutThick 		= new LengthParameter("Nut Thickness",2,[10,3])
	LengthParameter boltLength		= new LengthParameter("Bolt Length",10,[20,5])
	CSG bolt = new Cube( thickness.getMM(),
			boltDiam.getMM(),
			boltLength.getMM()+thickness.getMM())
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

ArrayList <CSG> generateServoHinge(String servoName, double eyePlateHeight){
	LengthParameter boltDiam 		= new LengthParameter("Bolt Diameter",2.5,[8,2])
	LengthParameter thickness 		= new LengthParameter("Material Thickness",3.5,[10,1])
	LengthParameter boltLength		= new LengthParameter("Bolt Length",10,[20,5])
	HashMap<String,Object> jawServoConfig = Vitamins.getConfiguration("hobbyServo",servoName)
	double servoHeightFromMechPlate = Double.parseDouble(jawServoConfig.get("servoThinDimentionThickness"))/2
	double widthOfTab = thickness.getMM()*4+boltDiam.getMM()
	CSG pinAssembly = new Cube(	thickness.getMM(),
							widthOfTab,
							eyePlateHeight-thickness.getMM()
	
		).toCSG()
		.toZMin()
		.movez(thickness.getMM())
		
	pinAssembly =tSlotPunch(	pinAssembly)
	pinAssembly =tSlotPunch(	pinAssembly.rotx(180).toZMin().movez(+thickness.getMM()))	

	pinAssembly=pinAssembly.toYMin()
				.union(new Cube(	thickness.getMM(),
								thickness.getMM()*2,
								thickness.getMM()
				).toCSG()
						.movez(servoHeightFromMechPlate+thickness.getMM())
						.movey(-thickness.getMM()/2)
						)
	def parts = [pinAssembly,pinAssembly
						.union(tSlotTabsWithHole()
							.movey(widthOfTab/2),
							tSlotTabsWithHole()
							.roty(180)
							.movez(eyePlateHeight+thickness.getMM())
							.movey(widthOfTab/2)
						)]

	return parts
}

ArrayList <CSG> generateUpperHead(CSG lowerHead){
	LengthParameter boltDiam 		= new LengthParameter("Bolt Diameter",4,[8,2])
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
	double backeHeadMount = 
	moutOffset = lowerHead.getMaxX()-thickness.getMM()*3
	upperHead = tSlotPunch(	upperHead				
	.movey(moutOffset)
	)
	.movey(-moutOffset)	

	upperHead = upperHead.rotz(-90)
	CSG 	upperHeadWithHoles = upperHead
							.union(tSlotTabsWithHole()
									.rotz(90)
									.movex(lowerHead.getMinX()+thickness.getMM()*3)	)
									.union(
										tSlotTabsWithHole()
										.rotz(90)
										.movex(lowerHead.getMaxX()-thickness.getMM()*3)	
										)
			
	def parts = [upperHead,upperHeadWithHoles].collect{
		it.movez(jawHeight.getMM())
	}

	return parts
}
CSGDatabase.clear()//set up the database to force only the default values in	
//return  makeHead().collect { it.prepForManufacturing() } //generate the cuttable file
return makeHead()