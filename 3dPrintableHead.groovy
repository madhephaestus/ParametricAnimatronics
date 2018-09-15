//Your code here
if (args==null){
	CSGDatabase.clear()
}
class HeadMakerClass implements IParameterChanged{
	LengthParameter printerOffset
	LengthParameter noseLength
	LengthParameter jawLength
	LengthParameter eyeDiam
	StringParameter servoSizeParam
	
	LengthParameter eyemechRadius
	StringParameter hornSizeParam
	LengthParameter eyeCenter 	
	LengthParameter noseDiameter 	
	StringParameter bearingSizeParam
	HashMap<String, Object>  boltData
	HashMap<String, Object>  servoData
	CSG horn 
	CSG servo 
	double servoThickness 
	double servoSeperation
	double servoY 
	double washerSize
	double headTotalWidth
	double servoSweep 
	double backBaseX 
	double frontBaseX
	double backOfEyes
	def eyePartsMaker=null
	def retparts=null
	double cornerRadius 
	double locationOfBackOfhead
	double boltLength 
	double bite
	double bearingHoleDiam
	double mountBoltDistance
	double servoNub
	CSG bitePart 
	CSG loosePart
	CSG headBolt 
	CSG boltStub 
	CSG mountBoltStub
	boolean debug =true
	public 	HeadMakerClass(){
		compute()				
	}
	void compute(){
		 printerOffset		= new LengthParameter("printerOffset",0.5,[2,0.001])
		 noseLength		= new LengthParameter("noseLength",20,[200,001])
		 jawLength		= new LengthParameter("jawLength",40,[200,001])
		 eyeDiam 		= new LengthParameter("Eye Diameter",40,[60,38])
		 servoSizeParam 			= new StringParameter("hobbyServo Default","DHV56mg_sub_Micro",Vitamins.listVitaminSizes("hobbyServo"))
		// servoSizeParam 			= new StringParameter("hobbyServo Default","towerProMG91",Vitamins.listVitaminSizes("hobbyServo"))
		 eyemechRadius		= new LengthParameter("Eye Mech Linkage",12,[20,5])
		 hornSizeParam 			= new StringParameter("hobbyServoHorn Default","DHV56mg_sub_Micro_1",Vitamins.listVitaminSizes("hobbyServoHorn"))
		// hornSizeParam 			= new StringParameter("hobbyServoHorn Default","standardMicro1",Vitamins.listVitaminSizes("hobbyServoHorn"))
		 eyeCenter 		= new LengthParameter("Eye Center Distance",eyeDiam.getMM()*1.5,[100,eyeDiam.getMM()])
		 noseDiameter 		= new LengthParameter("Nose Diameter",eyeDiam.getMM()*2,[eyeDiam.getMM()*3,10])
		 bearingSizeParam 			= new StringParameter("Bearing Size","608zz",Vitamins.listVitaminSizes("ballBearing"))
		 boltData = Vitamins.getConfiguration( "capScrew","M5")
		 servoData = Vitamins.getConfiguration( "hobbyServo",servoSizeParam.getStrValue())
					//.union(horn)
		 horn = Vitamins.get("hobbyServoHorn",hornSizeParam.getStrValue())	
						.roty(180).rotz(180+45).movez(1)
		 servo = Vitamins.get("hobbyServo",servoSizeParam.getStrValue())
				.toZMax()
				//.movez(1)
		 servoThickness = Math.abs(servo.getMinX())
		 servoSeperation = 4
		 servoY = Math.abs(servo.getTotalY())
		 washerSize = boltData.headDiameter/2+1.2
		 headTotalWidth = eyeCenter.getMM()+eyeDiam.getMM()+washerSize/2
		 servoSweep = 60
		 cornerRadius = 2
		 backBaseX =servoThickness*2+servoSeperation+cornerRadius+eyemechRadius.getMM()*2
		 frontBaseX =eyemechRadius.getMM()+servoThickness+4
		 backOfEyes = backBaseX+frontBaseX
		def eyePartsMaker=null
		def retparts=null
		 
		 locationOfBackOfhead = -backOfEyes-servoThickness+(cornerRadius*3)-(eyemechRadius.getMM()/2)
		 boltLength = 12
		 bite = boltLength/2
		 bearingHoleDiam = 8
		 mountBoltDistance =20
		 
		 servoNub = servoData.tipOfShaftToBottomOfFlange-
					   servoData.bottomOfFlangeToTopOfBody-
					   servoData.flangeThickness+
					   printerOffset.getMM()*2
		
		 bitePart =new Cylinder(boltData.outerDiameter/2,bite).toCSG()
		 loosePart =new Cylinder(boltData.outerDiameter/2+printerOffset.getMM(),boltLength-bite+1).toCSG()
					.movez(bite)
		 headBolt =new Cylinder(boltData.headDiameter/2+printerOffset.getMM(),100).toCSG()
					.movez(boltLength+1)
		boltStub = CSG.unionAll([bitePart,loosePart,headBolt])
					.roty(180)
					.movez(bite+1)
		 mountBoltStub = boltStub.union(boltStub.movex(-mountBoltDistance))
							.roty(180)
							.movey(eyeCenter.getMM()/2)
							.movex(locationOfBackOfhead+6)						
	}
		/**
	 * This is a listener for a parameter changing
	 * @param name
	 * @param p
	 */
	 
	public void parameterChanged(String name, Parameter p){
		//new RuntimeException().printStackTrace(System.out);
		//println "All Parts was set to null "+name
		if(retparts!=null){
			retparts=null
			try{
			compute()
			}catch(Exception e){}
		}
		
		
	}
	List<CSG> jawParts(){
		println servoData
		headTotalWidth = eyeCenter.getMM()+eyeDiam.getMM()+washerSize/2
		double overlap = 0
		double mountBlockX = servoThickness*2+cornerRadius
		double servoChordSideDistance = servo.getMaxY()
		double jawThickness = 6 
		double backBlockz = servoData.flangeLongDimention+cornerRadius
		
		CSG jawMount = new RoundedCube(mountBlockX+mountBoltDistance+boltData.headDiameter,
							boltData.headDiameter*2+cornerRadius,
							jawThickness+cornerRadius*2
							).cornerRadius(cornerRadius).toCSG()
							.toZMax()
							.toXMin()
							.movez(-eyeDiam.getMM()/2+cornerRadius*2)
							.movex(locationOfBackOfhead+overlap-mountBlockX)
							.movey(eyeCenter.getMM()/2)
		CSG jawServoBlock = new RoundedCube(mountBlockX,
							headTotalWidth,
							backBlockz
							).cornerRadius(cornerRadius).toCSG()
							.toYMin()
							.toXMax()
							.toZMin()
							.movez(-eyeDiam.getMM()/2)
							.movey(-eyeDiam.getMM()/2-washerSize/4)
							.movex(locationOfBackOfhead+overlap)
							
							.union([jawMount])
							
		CSG jawMountBolts = mountBoltStub
						.toZMin()
						.movez(jawServoBlock.getMinZ())
		jawServoBlock=jawServoBlock
			.difference(jawMountBolts)
		double jawXLocation =locationOfBackOfhead-servoThickness
		double jawYLocation =jawServoBlock.getMaxY()
		double jawBoltYLocation =jawServoBlock.getMinY()
		double jawZLocation =-eyeDiam.getMM()/2+servoChordSideDistance
		double jawLowerZ = jawZLocation -jawLength.getMM()
		double jawattachTHickness = 30
		CSG jawHorn = horn
					.rotz(-45-180)
					.movez(servoNub)
					.rotx(90)
		jawHorn=jawHorn.union([jawHorn.movey(1),jawHorn.movey(2),jawHorn.movey(3)])
					.move(jawXLocation,jawYLocation,jawZLocation)
		CSG JawServo = servo
					//.movez(servoNub)
					.rotx(90)
					.move(jawXLocation,jawYLocation,jawZLocation)
		CSG jawBolt = boltStub
					.rotx(90)
					.move(jawXLocation,jawBoltYLocation,jawZLocation)
					.union(boltStub
					.roty(-90)
					.move(locationOfBackOfhead+overlap,jawBoltYLocation+15,jawZLocation-5))
		jawServoBlock=jawServoBlock
			.difference([  JawServo,
						JawServo.movex(cornerRadius),
						JawServo.movex(cornerRadius*2),
						JawServo.movex(cornerRadius*3),
						JawServo.movex(cornerRadius*4)])	
		double jawWidth = headTotalWidth/2
		CSG backMountUpperJaw =new RoundedCube(mountBlockX,
							jawWidth*2,jawThickness)
							.cornerRadius(cornerRadius)
							.toCSG()
							.toZMin()
							
							
							
		CSG jawBlank = new Cylinder(noseDiameter.getMM()/2,jawThickness).toCSG()	
						.difference(new Cube(jawWidth*2).toCSG().toXMax())
						.toXMax()
						.movex(noseLength.getMM()+100)
						
		jawBlank=jawBlank.union(backMountUpperJaw)
						.hull()
						.movey(eyeCenter.getMM()/2)
		CSG cutter = jawBlank.scalez(10)
							.movez(-jawattachTHickness)
				
		cutter=cutter.toolOffset(-30)
		cutter=cutter.union(cutter.movex(-jawattachTHickness)).hull()
		double jawWidthOfLug = (servoThickness*2+cornerRadius*2)*2
		CSG jawLug = new RoundedCube(jawWidthOfLug,
							jawThickness+jawattachTHickness/2,
							jawThickness*3)
							.cornerRadius(cornerRadius).toCSG()
							.toZMin()
							.toYMax()
							.movey(jawThickness)
		CSG jawAttach= new RoundedCube(jawWidthOfLug,
							jawThickness,
							jawLength.getMM()+jawWidthOfLug/2)
							.cornerRadius(cornerRadius).toCSG()
							.toYMin()
							.toZMax()
							.movez(jawWidthOfLug/2)
					
		jawAttach=jawAttach.union(jawLug.movez(jawLowerZ-jawZLocation))
					//.intersect(new Cube(jawLength.getMM()*1.75).toCSG().movez(-jawLength.getMM()*0.25))
		double lugXAllignenment= jawServoBlock.getMinX()+jawLug.getMaxX()
		CSG driven = jawAttach
					.move(lugXAllignenment,jawYLocation,jawZLocation)
		CSG passive = jawAttach
					.rotz(180)
					.move(lugXAllignenment,jawBoltYLocation,jawZLocation)						
		CSG lowerJaw = jawBlank
						.difference(cutter)
						.toXMin()
						.move(jawServoBlock.getMinX(),0,jawLowerZ)
						.union(driven)
						.union(passive)
						.difference([jawBolt,JawServo,jawHorn])
						
		CSG uppweJaw = jawBlank
						.toZMax()
						.toXMin()
						.move(jawServoBlock.getMinX(),0,-eyeDiam.getMM()/2+cornerRadius)
						
		jawServoBlock=jawServoBlock
					.union(	uppweJaw)
					.difference([jawBolt,jawMountBolts])		
				
		BowlerStudioController.addCsg(jawServoBlock);
		BowlerStudioController.addCsg(lowerJaw);
				

		
		return [jawServoBlock,JawServo,jawBolt,lowerJaw,jawHorn]
	}
	List<CSG> make(){
		if(retparts != null)
			return retparts
	
		CSG washerHole =new Cylinder(bearingHoleDiam/2,boltLength-bite+1).toCSG()
		CSG washer =new Cylinder(washerSize,1).toCSG()
		CSG bearingAss = CSG.unionAll([washerHole,washer])
					.toZMax()
					.movez(1)
					.difference(boltStub)
		def kwPart = washerHole
		if(!debug){
			kwPart=kwPart	.toolOffset(printerOffset.getMM())
		}
		CSG bearingKeepawy= CSG.unionAll([kwPart,new Cylinder(washerSize+1,100).toCSG().toZMax().movez(1)])
						.toZMax()
						.movez(1)
		CSG bolt=boltStub.movez(-2)						
		//return [bolt]
		
		CSG bearing = bearingKeepawy
					
		

		
		Transform panServoLocation = new Transform()
								.translate(-servoThickness*2-eyemechRadius.getMM()*2-servoSeperation,
								0,
								0)
		Transform panBearingLocation =  new Transform()
								.translate(-servoThickness*2-eyemechRadius.getMM()*2-servoSeperation,
								eyeCenter.getMM(),
								0)

								
		Transform tiltServoLocation = new Transform()
								.translate(-eyemechRadius.getMM()*2,
								eyemechRadius.getMM(),
								eyemechRadius.getMM())
		Transform tiltBearingLocation =new Transform( )
								.translate(-eyemechRadius.getMM()*2,
								eyeCenter.getMM()+eyemechRadius.getMM(),
								eyemechRadius.getMM())
		CSG tiltServo = servo
					.movez(-servoNub)
					.transformed(tiltServoLocation)
		CSG panServo = servo
					.movez(-servoNub)
					//.roty(180)
					.transformed(panServoLocation)
		BowlerStudioController.setCsg([tiltServo,panServo]);
		
		if( eyePartsMaker==null)			
		eyePartsMaker= ScriptingEngine.gitScriptRun(
	                                "https://github.com/madhephaestus/ParametricAnimatronics.git", // git location of the library
		                              "EyeMaker.groovy" , // file to load
		                              []// no parameters (see next tutorial)
	                        )
	     println "Generate eyes..."
	     List<CSG> eyeParts =    eyePartsMaker.make(eyeDiam.getMM())  
	     println "Eyes made"    
		CSG eye = eyeParts.get(0)
		CSG lEye = eye.movey(eyeCenter.getMM())
		BowlerStudioController.addCsg(eye);
		BowlerStudioController.addCsg(lEye);
		def jawPartList = jawParts()
		
		
	    	CSG eyeMount = eyeParts.get(1)
	    	CSG eyeKeepawaCutter = eyeParts.get(2)
		CSG cup =  eyeParts.get(3)
		CSG slaveCup =  eyeParts.get(3)
					.movez(-eyemechRadius.getMM())	
					.movex(-eyemechRadius.getMM())	
		CSG cupPan =  eyeParts.get(3)
				.movez(-eyemechRadius.getMM())	
				.movey(-eyemechRadius.getMM())					
		CSG linkCup = cup.rotz(180)
					.movey(-eyemechRadius.getMM())
					.movez(-eyemechRadius.getMM())						
		CSG cupTiltSrv = linkCup
					.transformed(tiltServoLocation)
		CSG cupPanSrv	=linkCup
					.transformed(panServoLocation)
		BowlerStudioController.addCsg(cupTiltSrv);
		BowlerStudioController.addCsg(cupPanSrv);
		double cupThick = cup.getTotalZ()
		CSG linkPin =  eyeParts.get(4)
						.movez(-eyemechRadius.getMM())
		CSG servolinkPin=linkPin
				.roty(-90)
		servolinkPin=servolinkPin
				.intersect(servolinkPin.getBoundingBox().movez(1))
		CSG slaveLink=eyeParts.get(4)
					.rotx(90)
					.rotz(-90)
					.movey(eyemechRadius.getMM())
		servolinkPin=servolinkPin.union(	slaveLink
										.intersect(slaveLink.getBoundingBox().movez(1)))
				.movey(-eyemechRadius.getMM())
		CSG servolinkBlank= servolinkPin
				.union(horn)
				.hull()
		if(!debug){
			servolinkBlank=servolinkBlank.toolOffset(2)
		}
				
		CSG linkKeepaway  = new Sphere(6.5,30,7).toCSG()
		linkKeepaway=linkKeepaway.union(linkKeepaway.move(6,0,0).rotz(servoSweep/-2)).hull()
		linkKeepaway=linkKeepaway.union(linkKeepaway.move(6,0,0).rotz(-servoSweep/-2)).hull()
		linkKeepaway=linkKeepaway.union(linkKeepaway.movez(5)).hull()
		servolinkBlank=servolinkBlank.intersect(servolinkBlank.getBoundingBox().toZMin().movez(servolinkPin.getMinZ()))	
						.difference(linkKeepaway.movey(-eyemechRadius.getMM()))
						.difference(linkKeepaway.rotz(180).movex(-eyemechRadius.getMM()))
						.union(servolinkPin)
		CSG servoHornLinkage=servolinkBlank
						.difference(horn)
						.difference(horn.movez(1.5))
						.difference(horn.movez(2.5))
		def servoKw = servolinkBlank.getBoundingBox()
		if(!debug)
			servoKw=servoKw.toolOffset(1)
		CSG linkageKeepaway = CSG.unionAll(
		Extrude.revolve(servoKw,
		(double)0, // rotation center radius, if 0 it is a circle, larger is a donut. Note it can be negative too
		(double)servoSweep,// degrees through wich it should sweep
		(int)10)//number of sweep increments
		).rotz(servoSweep/-2)
		CSG eyeKeepaway = CSG.unionAll(
		Extrude.revolve(eye.getBoundingBox().toolOffset(debug?0:1),
		(double)0, // rotation center radius, if 0 it is a circle, larger is a donut. Note it can be negative too
		(double)servoSweep,// degrees through wich it should sweep
		(int)10)//number of sweep increments
		).rotz(servoSweep/-2)
		eyeKeepaway=eyeKeepaway.union(eyeKeepaway.rotx(90))
			.intersect(new Sphere(eyeDiam.getMM()/2+4).toCSG())
		CSG beringLinkage = 	servolinkBlank		
							.difference(bolt)
		CSG aSlice = slaveCup.intersect(slaveCup.getBoundingBox().toXMax().movex(slaveCup.getMinX()+cupThick))
		CSG bar = aSlice.union(aSlice.movey(eyeCenter.getMM())).hull()
					.movex(-2)
		CSG slaveLinkage = slaveCup.union(slaveCup.movey(eyeCenter.getMM()))
						.union(bar)
		CSG panLinkage = makeLinkage(cupPanSrv,cupPan)
		CSG tiltLinkage = makeLinkage(cupTiltSrv,cup)	
		println "Making Linkage keepaways"			
		CSG slavelinkageKeepaway=CSG.unionAll([slaveLinkage.getBoundingBox().toolOffset(debug?0:1),
							slaveLinkage.getBoundingBox().toolOffset(debug?0:1)
							.move(Math.sin(Math.toRadians(servoSweep/2))*eyemechRadius.getMM(),
								Math.cos(Math.toRadians(servoSweep/2))*eyemechRadius.getMM(),
								0
								),
							slaveLinkage.getBoundingBox().toolOffset(debug?0:1)
							.move(Math.sin(Math.toRadians(servoSweep/2))*eyemechRadius.getMM(),
								-Math.cos(Math.toRadians(servoSweep/2))*eyemechRadius.getMM(),
								0
								)
							]).hull()
		CSG panKeepaway = panLinkage.getBoundingBox()
				.movex(eyemechRadius.getMM()*2)
				.movey(-eyemechRadius.getMM()*0.85)
		CSG panlinkageKeepaway=CSG.unionAll([panKeepaway.toolOffset(debug?0:1),
							panKeepaway.toolOffset(debug?0:1)
							.move(Math.sin(Math.toRadians(servoSweep/2))*eyemechRadius.getMM(),
								Math.cos(Math.toRadians(servoSweep/2))*eyemechRadius.getMM(),
								0
								),
							panKeepaway.toolOffset(debug?0:1)
							.move(-Math.sin(Math.toRadians(servoSweep/2))*eyemechRadius.getMM(),
								Math.cos(Math.toRadians(servoSweep/2))*eyemechRadius.getMM(),
								0
								)
							]).hull()
		linkageKeepaway=CSG.unionAll([linkageKeepaway,
					slavelinkageKeepaway,
					linkageKeepaway.movey(eyeCenter.getMM()),
					panlinkageKeepaway,
					panlinkageKeepaway.movey(eyeCenter.getMM())					
					
		])
		bearing=bearing.movez(linkageKeepaway.getMinZ())
		bolt=bolt.movez(linkageKeepaway.getMinZ())
		bearingAss=bearingAss.movez(linkageKeepaway.getMinZ())
		// Allign the linkages
		CSG panTotalLinkageKeepaway =	linkageKeepaway.transformed(panServoLocation)
		CSG tiltTotalLinkageKeepaway =	linkageKeepaway.transformed(tiltServoLocation)
		
		
		CSG panBolt =	bolt.transformed(panBearingLocation)
		CSG tiltBolt =	bolt.transformed(tiltBearingLocation)
		
		CSG slaveLinkagePan = slaveLinkage.transformed(panServoLocation)
		CSG slaveLinkageTilt = slaveLinkage.transformed(tiltServoLocation)
		BowlerStudioController.addCsg(slaveLinkagePan);
		BowlerStudioController.addCsg(slaveLinkageTilt);
		CSG linkPinTiltBearing =beringLinkage
				.transformed(tiltBearingLocation)
		CSG linkPinPanBearing =beringLinkage
				.transformed(panBearingLocation)				
		CSG linkPinTilt =servoHornLinkage
				.transformed(tiltServoLocation)
		CSG linkPinPan =servoHornLinkage
				.transformed(panServoLocation)
		
		BowlerStudioController.addCsg(slaveLinkagePan);
		BowlerStudioController.addCsg(slaveLinkageTilt);
		BowlerStudioController.addCsg(linkPinTilt);
		BowlerStudioController.addCsg(linkPinPan);
		
		CSG panBearingPart = bearingAss.transformed(panBearingLocation)	
		CSG tiltBearingPart = bearingAss.transformed(tiltBearingLocation)
		CSG panBearing = bearing.transformed(panBearingLocation)	
		CSG tiltBearing = bearing.transformed(tiltBearingLocation)	
		
		println "Begin building head base"
		CSG frontBase = new RoundedCube(frontBaseX,
							eyeCenter.getMM()+eyeDiam.getMM()+washerSize/2,
							eyeDiam.getMM()/2+linkageKeepaway.getMinZ() + eyemechRadius.getMM())
						.cornerRadius(cornerRadius).toCSG()
							.toZMax()
							.toXMin()
							.toYMin()
							.movez(linkageKeepaway.getMinZ()+eyemechRadius.getMM())
							.movex(-eyemechRadius.getMM()*2-servoThickness)
							.movey(-eyeDiam.getMM()/2-washerSize/4)
		CSG servoSupport = new RoundedCube(servoSeperation+4,
							eyeDiam.getMM()/2-(38.0/2.0-18.0),
							eyeDiam.getMM()/2+linkageKeepaway.getMinZ() 
							).cornerRadius(cornerRadius).toCSG()
							.toZMin()
							.toXMax()
							.toYMin()
							.movez(frontBase.getMinZ())
							.movex(frontBase.getMinX()+4)
							.movey(frontBase.getMinY())
		
		CSG backtBase = new RoundedCube(backBaseX,
							eyeCenter.getMM()+eyeDiam.getMM()+washerSize/2,
							eyeDiam.getMM()/2+linkageKeepaway.getMinZ()
							).cornerRadius(cornerRadius).toCSG()
							.toZMin()
							.toXMax()
							.toYMin()
							.movez(frontBase.getMinZ())
							.movex(frontBase.getMinX())
							.movey(frontBase.getMinY())
		CSG bearingSupport = new RoundedCube(backBaseX,
							bearing.getTotalY()+2,	
							eyeDiam.getMM()/2+linkageKeepaway.getMinZ() + eyemechRadius.getMM()
							)
							.cornerRadius(cornerRadius)
							.toCSG()
							.toZMin()
							.toXMax()
							.toYMax()
							.movez(frontBase.getMinZ())
							.movex(frontBase.getMinX())
							.movey(frontBase.getMaxY())
		CSG attachmentBolt = bolt
						.movez(5)
						.roty(-90).toZMin()
						.movey(eyeCenter.getMM()-bolt.getMaxY()*2-2.5)
						.movex(frontBase.getMinX()+5)	
						.movez(frontBase.getMinZ()+2)		
		CSG MountBolts = mountBoltStub	
						.toZMin()
						.movez(backtBase.getMaxZ()-boltLength-boltData.headHeight)							
		println "Making head"
		CSG head = frontBase
					.union(servoSupport)
					.difference([
					tiltServo,panServo,
					tiltServo.movex(-2),
					eyeKeepaway,
					eyeKeepaway.movey(eyeCenter.getMM()),
					tiltBearing,panBearing,
					panTotalLinkageKeepaway,tiltTotalLinkageKeepaway,
					attachmentBolt,MountBolts
					])	
		BowlerStudioController.addCsg(head);
		CSG eyesKeepaway = 	CSG.unionAll([eyeKeepaway,
					eyeKeepaway.movey(eyeCenter.getMM())])			
		CSG headBack = backtBase
					.union(bearingSupport)
					.toXMin()
					.movex(locationOfBackOfhead)
					.difference([
					head,
					frontBase,servoSupport,
					tiltServo,panServo,
					panServo.movex(servoSeperation),
					panServo.movex(servoSeperation*1.5),
					panServo.movex(servoSeperation*2),
					eyesKeepaway,
					tiltBearing,
					panBearing,
					panTotalLinkageKeepaway,tiltTotalLinkageKeepaway,
					attachmentBolt,MountBolts
					])	
		BowlerStudioController.addCsg(headBack);
		println headBack.getTotalY()
		CSG eyestockPin = new Cylinder(eyeKeepawaCutter.getMaxY(),6).toCSG()
						.roty(-90)
						.movex(-eyemechRadius.getMM())
		CSG eyeStockShaft = new RoundedCube(backBaseX,
									eyeKeepawaCutter.getTotalY(),
									eyeKeepawaCutter.getTotalY())
									.cornerRadius(1)
									.toCSG()
									.toXMax()
									.movex(-eyemechRadius.getMM())
		eyestockPin=eyestockPin.union(eyeStockShaft)
		CSG box = eyestockPin.getBoundingBox()
		CSG eyestockPinUpper = eyestockPin.intersect(box.toZMax())
		CSG eyestockPinLower = eyestockPin.intersect(box.toZMin())
		
		CSG eyestockPinUpperS = eyestockPinUpper.intersect(head).union(eyeMount.rotx(180))
										.difference(tiltServo.movey(-2))
										.difference(servoSupport)
		CSG eyestockPinLowerS = eyestockPinLower.intersect(head).union(eyeMount)
										.difference(tiltServo.movey(-2))
		CSG eyestockPinUpperB = eyestockPinUpper.movey(eyeCenter.getMM()).intersect(head).union(eyeMount.rotx(180).movey(eyeCenter.getMM()))
		CSG eyestockPinLowerB = eyestockPinLower.movey(eyeCenter.getMM()).intersect(head).union(eyeMount.movey(eyeCenter.getMM()))
		head=head.minkowskiDifference(eyestockPin,printerOffset.getMM()*2)
		.minkowskiDifference(eyestockPin.movey(eyeCenter.getMM()),printerOffset.getMM()*2)
		
		CSG ltiltLinkage=tiltLinkage.movey(eyeCenter.getMM())
		CSG llinkPinTilt=panLinkage.movey(eyeCenter.getMM())

		
		CSG jaw = jawPartList[3]
		CSG servoBlock = jawPartList[0]
						.difference([head.hull(),headBack.hull(),panTotalLinkageKeepaway,tiltTotalLinkageKeepaway,
					attachmentBolt,MountBolts,
					tiltServo,panServo,eyesKeepaway
						])
		CSG jawBolts = 	jawPartList[2]		
		CSG jawServo = 	jawPartList[1]	
		headBack=headBack.difference(jawBolts)
		jaw.setName("jaw")
		jaw.setManufacturing({ toMfg ->
			return toMfg.toZMin()
		})
		jawServo.setManufacturing({ toMfg ->
			return null
		})
		jawBolts.setManufacturing({ toMfg ->
			return null
		})
		servoBlock.setName("JawServoBlock")
		servoBlock.setManufacturing({ toMfg ->
			return toMfg
					.roty(90)// fix the orentation
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		eye.setManufacturing({ toMfg ->
			return toMfg
					.roty(90)// fix the orentation
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		lEye.setManufacturing({ toMfg ->
			return toMfg
					.roty(90)// fix the orentation
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		eyestockPinUpperS.setManufacturing({ toMfg ->
			return toMfg
					.rotx(-180)// fix the orentation
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		eyestockPinLowerS.setManufacturing({ toMfg ->
			return toMfg
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		eyestockPinUpperB.setManufacturing({ toMfg ->
			return toMfg
					.rotx(-180)// fix the orentation
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		eyestockPinLowerB.setManufacturing({ toMfg ->
			return toMfg
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		ltiltLinkage.setManufacturing({ toMfg ->
			return toMfg
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		llinkPinTilt.setManufacturing({ toMfg ->
			return toMfg
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		head.setName("frontOfHead")
		head.setManufacturing({ toMfg ->
			return toMfg
					.roty(-90)
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		headBack.setName("headBack")
		headBack.setManufacturing({ toMfg ->
			return toMfg
					.roty(90)
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		tiltLinkage.setManufacturing({ toMfg ->
			return toMfg
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		panLinkage.setManufacturing({ toMfg ->
			return toMfg
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		linkPinTilt.setManufacturing({ toMfg ->
			return toMfg
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		linkPinPan.setManufacturing({ toMfg ->
			return toMfg
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		
		linkPinTiltBearing.setManufacturing({ toMfg ->
			return toMfg
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		linkPinPanBearing.setManufacturing({ toMfg ->
			return toMfg
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		slaveLinkagePan.setManufacturing({ toMfg ->
			return toMfg
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		slaveLinkageTilt.setManufacturing({ toMfg ->
			return toMfg
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		tiltBearingPart.setManufacturing({ toMfg ->
			return toMfg
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		panBearingPart.setManufacturing({ toMfg ->
			return toMfg
					.toZMin()//move it down to the flat surface
					.toXMin()
					.toYMin()
		})
		retparts= [
		//tiltServo,panServo,
		jaw,
		jawServo,
		servoBlock,
		eye,lEye,
		tiltLinkage,panLinkage,
		linkPinTilt,linkPinPan,
		linkPinTiltBearing,linkPinPanBearing,
		slaveLinkagePan,slaveLinkageTilt,
		tiltBearingPart,panBearingPart,
		//panTotalLinkageKeepaway,tiltTotalLinkageKeepaway,
		head,headBack,
		eyestockPinUpperS,eyestockPinLowerS,eyestockPinUpperB,eyestockPinLowerB,
		ltiltLinkage,llinkPinTilt,
		//attachmentBolt//,MountBolts
		]//.collect{it.prepForManufacturing()}
		def params =[printerOffset,eyeDiam,servoSizeParam,eyemechRadius,hornSizeParam,eyeCenter,noseLength,jawLength,noseDiameter]
		for(int i = 0;i< retparts.size();i++){
			int index = i;
			retparts.get(i).setRegenerate({return make().get(index)})
			params.collect{
				retparts.get(i).setParameter(it)
			}
		}
		params.collect{
			CSGDatabase.addParameterListener(it.getName() ,this);
		}
		return retparts
	}
	
	CSG makeLinkage(CSG a, CSG b){
		CSG aSlice = a.intersect(a.getBoundingBox().toXMin().movex(a.getMaxX()-1))
		CSG bSlice = b.intersect(b.getBoundingBox().toXMax().movex(b.getMinX()+1))
		CSG bar =aSlice.union(bSlice).hull()
		return CSG.unionAll([a,b,bar])
	}
}
//println new HeadMakerClass().metaClass.methods*.name.sort().unique()  
def maker = new HeadMakerClass()
//return [maker.jawParts()]
return [maker.make()]