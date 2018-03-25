//Your code here
if (args==null){
	CSGDatabase.clear()
}
class HeadMakerClass{
	LengthParameter printerOffset		= new LengthParameter("printerOffset",0.5,[2,0.001])
	LengthParameter eyeDiam 		= new LengthParameter("Eye Diameter",38,[60,38])
	StringParameter servoSizeParam 			= new StringParameter("hobbyServo Default","DHV56mg_sub_Micro",Vitamins.listVitaminSizes("hobbyServo"))
	LengthParameter eyemechRadius		= new LengthParameter("Eye Mech Linkage",12,[20,5])
	StringParameter hornSizeParam 			= new StringParameter("hobbyServoHorn Default","standardMicro1",Vitamins.listVitaminSizes("hobbyServoHorn"))
	LengthParameter eyeCenter 		= new LengthParameter("Eye Center Distance",50,[100,50])
	StringParameter bearingSizeParam 			= new StringParameter("Bearing Size","608zz",Vitamins.listVitaminSizes("ballBearing"))
	double servoSweep = 60
	List<CSG> make(){
		CSG bearing = Vitamins.get("ballBearing",bearingSizeParam.getStrValue())
					.toZMax()
					
		CSG horn = Vitamins.get("hobbyServoHorn",hornSizeParam.getStrValue())	
					.roty(180).rotz(180+45).movez(1.5)
		CSG servo = Vitamins.get("hobbyServo",servoSizeParam.getStrValue())
					.toZMax()
					//.union(horn)
		double servoThickness = Math.abs(servo.getMinX())
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
					
		def eyePartsMaker= ScriptingEngine.gitScriptRun(
	                                "https://github.com/madhephaestus/ParametricAnimatronics.git", // git location of the library
		                              "EyeMaker.groovy" , // file to load
		                              []// no parameters (see next tutorial)
	                        )
	     println "Generate eyes..."
	     List<CSG> eyeParts =    eyePartsMaker.make(38)  
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
		CSG linkKeepaway  = new Sphere(6,30,7).toCSG()
		linkKeepaway=linkKeepaway.union(linkKeepaway.move(6,0,0).rotz(servoSweep/-2)).hull()
		linkKeepaway=linkKeepaway.union(linkKeepaway.movez(5)).hull()
		servolinkBlank=servolinkBlank.intersect(servolinkBlank.getBoundingBox().toZMin().movez(servolinkPin.getMinZ()))	
						.difference(linkKeepaway.movey(-eyemechRadius.getMM()))
						.difference(linkKeepaway.rotz(180).movex(-eyemechRadius.getMM()))
						.union(servolinkPin)
		CSG servoHornLinkage=servolinkBlank
						.difference(horn)
						.difference(horn.movez(1.5))
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
				.movey(-eyemechRadius.getMM()*0.75)
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

		// Allign the linkages
		CSG panTotalLinkageKeepaway =	linkageKeepaway.transformed(panServoLocation)
		CSG tiltTotalLinkageKeepaway =	linkageKeepaway.transformed(tiltServoLocation)
		
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
		
		
		
		CSG panBearing = bearing.transformed(panBearingLocation)	
		CSG tiltBearing = bearing.transformed(tiltBearingLocation)	
		
		// Begin building head base
		CSG frontBase = new Cube(eyeDiam.getMM()/2+2,
							eyeCenter.getMM()+eyeDiam.getMM()+bearing.getTotalY()/2,
							eyeDiam.getMM()/2+linkageKeepaway.getMinZ() + eyemechRadius.getMM()
							).toCSG()
							.toZMax()
							.toXMin()
							.toYMin()
							.movez(linkageKeepaway.getMinZ()+eyemechRadius.getMM())
							.movex(-eyemechRadius.getMM()*2-servoThickness)
							.movey(-eyeDiam.getMM()/2-bearing.getTotalY()/4)
		CSG servoSupport = new Cube(servoSeperation,
							20,
							eyeDiam.getMM()/2+linkageKeepaway.getMinZ() 
							).toCSG()
							.toZMin()
							.toXMax()
							.toYMin()
							.movez(frontBase.getMinZ())
							.movex(frontBase.getMinX())
							.movey(frontBase.getMinY())
		double backBaseX =eyeDiam.getMM()*0.75
		CSG backtBase = new Cube(backBaseX,
							eyeCenter.getMM()+eyeDiam.getMM()+bearing.getTotalY()/2,
							eyeDiam.getMM()/2+linkageKeepaway.getMinZ()
							).toCSG()
							.toZMin()
							.toXMax()
							.toYMin()
							.movez(frontBase.getMinZ())
							.movex(frontBase.getMinX())
							.movey(frontBase.getMinY())
		CSG bearingSupport = new Cube(backBaseX,
							bearing.getTotalY()+2,	
							eyeDiam.getMM()/2+linkageKeepaway.getMinZ() + eyemechRadius.getMM()
							).toCSG()
							.toZMin()
							.toXMax()
							.toYMax()
							.movez(frontBase.getMinZ())
							.movex(frontBase.getMinX())
							.movey(frontBase.getMaxY())
		println "Making head"
		CSG head = frontBase
					.union(servoSupport)
					.difference([
					tiltServo,panServo,
					eyeKeepaway,
					eyeKeepaway.movey(eyeCenter.getMM()),
					tiltBearing,panBearing,
					panTotalLinkageKeepaway,tiltTotalLinkageKeepaway,
					])					
		CSG headBack = backtBase
					.union(bearingSupport)
					.difference([
					frontBase,servoSupport,
					tiltServo,panServo,
					panServo.movex(servoSeperation),
					panServo.movex(servoSeperation*1.5),
					eyeKeepaway,
					eyeKeepaway.movey(eyeCenter.getMM()),
					tiltBearing,
					panBearing.union(panBearing.movex(bearing.getTotalY())).hull(),
					panTotalLinkageKeepaway,tiltTotalLinkageKeepaway,
					])	
		println headBack.getTotalY()
		CSG eyestockPin = new Cylinder(eyeKeepawaCutter.getMaxY(),4).toCSG()
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
		CSG eyestockPinLowerS = eyestockPinLower.intersect(head).union(eyeMount)
		CSG eyestockPinUpperB = eyestockPinUpper.movey(eyeCenter.getMM()).intersect(head).union(eyeMount.rotx(180).movey(eyeCenter.getMM()))
		CSG eyestockPinLowerB = eyestockPinLower.movey(eyeCenter.getMM()).intersect(head).union(eyeMount.movey(eyeCenter.getMM()))
		head=head.minkowskiDifference(eyestockPin,printerOffset.getMM())
		.minkowskiDifference(eyestockPin.movey(eyeCenter.getMM()),printerOffset.getMM())
		return [
		//tiltServo,panServo,
		eye,eyeMount,
		tiltLinkage,linkPinTilt,
		linkPinPan,panLinkage,
		linkPinTiltBearing,linkPinPanBearing,
		slaveLinkagePan,slaveLinkageTilt,
		//tiltBearing,panBearing,
		//panTotalLinkageKeepaway,tiltTotalLinkageKeepaway,
		head,headBack,
		eyestockPinUpperS,eyestockPinLowerS,eyestockPinUpperB,eyestockPinLowerB
		]
	}
	CSG makeLinkage(CSG a, CSG b){
		CSG aSlice = a.intersect(a.getBoundingBox().toXMin().movex(a.getMaxX()-1))
		CSG bSlice = b.intersect(b.getBoundingBox().toXMax().movex(b.getMinX()+1))
		CSG bar =aSlice.union(bSlice).hull()
		return CSG.unionAll([a,b,bar])
	}
}

return new HeadMakerClass().make()