//Your code here
if (args==null){
	CSGDatabase.clear()
}
class HeadMakerClass implements IParameterChanged{
	LengthParameter printerOffset		= new LengthParameter("printerOffset",0.3,[2,0.001])
	LengthParameter noseLength		= new LengthParameter("noseLength",10,[200,001])
	LengthParameter jawLength		= new LengthParameter("jawLength",40,[200,001])
	LengthParameter eyeDiam 		= new LengthParameter("Eye Diameter",40,[60,38])
	StringParameter servoSizeParam 			= new StringParameter("hobbyServo Default","DHV56mg_sub_Micro",Vitamins.listVitaminSizes("hobbyServo"))
	//StringParameter servoSizeParam 			= new StringParameter("hobbyServo Default","towerProMG91",Vitamins.listVitaminSizes("hobbyServo"))
	LengthParameter eyemechRadius		= new LengthParameter("Eye Mech Linkage",12,[20,5])
	StringParameter hornSizeParam 			= new StringParameter("hobbyServoHorn Default","DHV56mg_sub_Micro_1",Vitamins.listVitaminSizes("hobbyServoHorn"))
	//StringParameter hornSizeParam 			= new StringParameter("hobbyServoHorn Default","standardMicro1",Vitamins.listVitaminSizes("hobbyServoHorn"))
	LengthParameter eyeCenter 		= new LengthParameter("Eye Center Distance",43,[100,50])
	StringParameter bearingSizeParam 			= new StringParameter("Bearing Size","608zz",Vitamins.listVitaminSizes("ballBearing"))
	HashMap<String, Object>  boltData = Vitamins.getConfiguration( "capScrew","M5")
	HashMap<String, Object>  servoData = Vitamins.getConfiguration( "hobbyServo",servoSizeParam.getStrValue())
				//.union(horn)
	CSG horn = Vitamins.get("hobbyServoHorn",hornSizeParam.getStrValue())	
					.roty(180).rotz(180+45).movez(1)
	CSG servo = Vitamins.get("hobbyServo",servoSizeParam.getStrValue())
			.toZMax()
			.movez(1)
	double servoThickness = Math.abs(servo.getMinX())
	double servoY = Math.abs(servo.getTotalY())
	double washerSize = boltData.headDiameter/2+1.2
	double headTotalWidth = eyeCenter.getMM()+eyeDiam.getMM()+washerSize/2
	double servoSweep = 60
	double backBaseX =eyeDiam.getMM()*1.1
	double frontBaseX =eyeDiam.getMM()/2
	double backOfEyes = backBaseX+frontBaseX
	def eyePartsMaker=null
	def retparts=null
	double cornerRadius = 2
	double locationOfBackOfhead = -backOfEyes-servoThickness+(cornerRadius*3)-(eyemechRadius.getMM()/2)
	double boltLength = 12
	double bite = boltLength/2
	double bearingHoleDiam = 8
	double mountBoltDistance =20
	double servoNub = servoData.tipOfShaftToBottomOfFlange-
				   servoData.bottomOfFlangeToTopOfBody-
				   servoData.flangeThickness+
				   printerOffset.getMM()*2
	
	CSG bitePart =new Cylinder(boltData.outerDiameter/2,bite).toCSG()
	CSG loosePart =new Cylinder(boltData.outerDiameter/2+printerOffset.getMM(),boltLength-bite+1).toCSG()
				.movez(bite)
	CSG headBolt =new Cylinder(boltData.headDiameter/2+printerOffset.getMM(),100).toCSG()
				.movez(boltLength+1)
	CSG boltStub = CSG.unionAll([bitePart,loosePart,headBolt])
				.roty(180)
				.movez(bite+1)
	CSG mountBoltStub = boltStub.union(boltStub.movex(-mountBoltDistance))
						.roty(180)
						.movey(eyeCenter.getMM()/2)
						.movex(locationOfBackOfhead+6)	
						
		/**
	 * This is a listener for a parameter changing
	 * @param name
	 * @param p
	 */
	 
	public void parameterChanged(String name, Parameter p){
		//new RuntimeException().printStackTrace(System.out);
		//println "All Parts was set to null "+name
		retparts=null
	}
	List<CSG> jawParts(){
		println servoData
		
		double overlap = cornerRadius+4
		double mountBlockX = eyeDiam.getMM()/2
		double servoChordSideDistance = servo.getMaxY()
		double jawThickness = 6 
		
		CSG jawMount = new RoundedCube(mountBlockX+mountBoltDistance+boltData.headDiameter,
							boltData.headDiameter*2+cornerRadius,
							jawThickness+cornerRadius*2
							).cornerRadius(cornerRadius).toCSG()
							.toZMax()
							.toXMin()
							.movez(-eyeDiam.getMM()/2+cornerRadius*2)
							.movex(locationOfBackOfhead+overlap-eyeDiam.getMM()/2)
							.movey(eyeCenter.getMM()/2)
		CSG jawServoBlock = new RoundedCube(mountBlockX,
							headTotalWidth,
							eyeDiam.getMM()
							).cornerRadius(cornerRadius).toCSG()
							.toYMin()
							.toXMax()
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
					.movez(servoNub)
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
		CSG jawBlank = new Cylinder(jawWidth,jawThickness).toCSG()	
						.difference(new Cube(jawWidth*2).toCSG().toXMax())
						.movex(noseLength.getMM()+50)
						.union(new RoundedCube(mountBlockX,
							jawWidth*2,jawThickness).cornerRadius(cornerRadius).toCSG().toZMin())
						.hull()
						.movey(eyeCenter.getMM()/2)
		CSG cutter = jawBlank.scalez(10)
							.movez(-jawattachTHickness)
									.toolOffset(-30)
		cutter=cutter.union(cutter.movex(-jawattachTHickness)).hull()
		CSG jawLug = new RoundedCube(30,
							jawThickness+jawattachTHickness/2,jawThickness*3).cornerRadius(cornerRadius).toCSG()
							.toZMin()
							.toYMax()
							.movey(jawThickness)
		CSG jawAttach= new Cylinder(eyeDiam.getMM()/2+(-jawZLocation),jawThickness).toCSG()
					.rotx(90)
		jawAttach=jawAttach.union(jawLug.movez(jawLowerZ-jawZLocation))
					//.intersect(new Cube(jawLength.getMM()*1.75).toCSG().movez(-jawLength.getMM()*0.25))
		CSG driven = jawAttach
					.move(jawXLocation,jawYLocation,jawZLocation)
		CSG passive = jawAttach
					.rotz(180)
					.move(jawXLocation,jawBoltYLocation,jawZLocation)						
		CSG lowerJaw = jawBlank
						.difference(cutter)
						.move(jawXLocation,0,jawLowerZ)
						.union(driven)
						.union(passive)
						.difference([jawBolt,JawServo,jawHorn])
						
		CSG uppweJaw = jawBlank
						.toZMax()
						.move(jawXLocation,0,-eyeDiam.getMM()/2+cornerRadius)
		jawServoBlock=jawServoBlock
					.union(	uppweJaw)
					.difference(jawBolt)		
		

		

		
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
					
		CSG bearingKeepawy= CSG.unionAll([washerHole.toolOffset(printerOffset.getMM()),new Cylinder(washerSize+1,100).toCSG().toZMax().movez(1)])
						.toZMax()
						.movez(1)
		CSG bolt=boltStub.movez(-2)						
		//return [bolt]
		
		CSG bearing = bearingKeepawy
					
		

		double servoSeperation = 4
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
					.transformed(tiltServoLocation)
		CSG panServo = servo
					//.roty(180)
					.transformed(panServoLocation)
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
				.toolOffset(2)
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
		CSG linkageKeepaway = CSG.unionAll(
		Extrude.revolve(servolinkBlank.getBoundingBox().toolOffset(1),
		(double)0, // rotation center radius, if 0 it is a circle, larger is a donut. Note it can be negative too
		(double)servoSweep,// degrees through wich it should sweep
		(int)10)//number of sweep increments
		).rotz(servoSweep/-2)
		CSG eyeKeepaway = CSG.unionAll(
		Extrude.revolve(eye.getBoundingBox().toolOffset(1),
		(double)0, // rotation center radius, if 0 it is a circle, larger is a donut. Note it can be negative too
		(double)servoSweep,// degrees through wich it should sweep
		(int)10)//number of sweep increments
		).rotz(servoSweep/-2)
		eyeKeepaway=eyeKeepaway.union(eyeKeepaway.rotx(90))
			.intersect(new Sphere(eyeDiam.getMM()/2+1).toCSG())
		CSG beringLinkage = 	servolinkBlank		
							.difference(bolt)
		CSG aSlice = slaveCup.intersect(slaveCup.getBoundingBox().toXMax().movex(slaveCup.getMinX()+cupThick))
		CSG bar = aSlice.union(aSlice.movey(eyeCenter.getMM())).hull()
					.movex(-2)
		CSG slaveLinkage = slaveCup.union(slaveCup.movey(eyeCenter.getMM()))
						.union(bar)
		CSG panLinkage = makeLinkage(cupPanSrv,cupPan)
		CSG tiltLinkage = makeLinkage(cupTiltSrv,cup)				
		CSG slavelinkageKeepaway=CSG.unionAll([slaveLinkage.getBoundingBox().toolOffset(1),
							slaveLinkage.getBoundingBox().toolOffset(1)
							.move(Math.sin(Math.toRadians(servoSweep/2))*eyemechRadius.getMM(),
								Math.cos(Math.toRadians(servoSweep/2))*eyemechRadius.getMM(),
								0
								),
							slaveLinkage.getBoundingBox().toolOffset(1)
							.move(Math.sin(Math.toRadians(servoSweep/2))*eyemechRadius.getMM(),
								-Math.cos(Math.toRadians(servoSweep/2))*eyemechRadius.getMM(),
								0
								)
							]).hull()
		CSG panKeepaway = panLinkage.getBoundingBox()
				.movex(eyemechRadius.getMM()*2)
				.movey(-eyemechRadius.getMM()*0.85)
		CSG panlinkageKeepaway=CSG.unionAll([panKeepaway.toolOffset(1),
							panKeepaway.toolOffset(1)
							.move(Math.sin(Math.toRadians(servoSweep/2))*eyemechRadius.getMM(),
								Math.cos(Math.toRadians(servoSweep/2))*eyemechRadius.getMM(),
								0
								),
							panKeepaway.toolOffset(1)
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
		CSG linkPinTiltBearing =beringLinkage
				.transformed(tiltBearingLocation)
		CSG linkPinPanBearing =beringLinkage
				.transformed(panBearingLocation)				
		CSG linkPinTilt =servoHornLinkage
				.transformed(tiltServoLocation)
		CSG linkPinPan =servoHornLinkage
				.transformed(panServoLocation)
		
		
		CSG panBearingPart = bearingAss.transformed(panBearingLocation)	
		CSG tiltBearingPart = bearingAss.transformed(tiltBearingLocation)
		CSG panBearing = bearing.transformed(panBearingLocation)	
		CSG tiltBearing = bearing.transformed(tiltBearingLocation)	
		
		// Begin building head base
		CSG frontBase = new RoundedCube(eyeDiam.getMM()/2,
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
		CSG lEye = eye.movey(eyeCenter.getMM())
		CSG ltiltLinkage=tiltLinkage.movey(eyeCenter.getMM())
		CSG llinkPinTilt=panLinkage.movey(eyeCenter.getMM())

		def jawPartList = jawParts()
		CSG jaw = jawPartList[3]
		CSG servoBlock = jawPartList[0]
						.difference([head.hull(),headBack.hull(),panTotalLinkageKeepaway,tiltTotalLinkageKeepaway,
					attachmentBolt,MountBolts,
					tiltServo,panServo,eyesKeepaway
						])
		CSG jawBolts = 	jawPartList[2]		
		CSG jawServo = 	jawPartList[1]	
		headBack=headBack.difference(jawBolts)
		jaw.swtName("jaw")
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
		def params =[printerOffset,eyeDiam,servoSizeParam,eyemechRadius,hornSizeParam,eyeCenter]
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