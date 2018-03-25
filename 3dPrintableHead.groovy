//Your code here
if (args==null){
	CSGDatabase.clear()
}
class HeadMakerClass{
	StringParameter servoSizeParam 			= new StringParameter("hobbyServo Default","DHV56mg_sub_Micro",Vitamins.listVitaminSizes("hobbyServo"))
	LengthParameter eyemechRadius		= new LengthParameter("Eye Mech Linkage",12,[20,5])
	StringParameter hornSizeParam 			= new StringParameter("hobbyServoHorn Default","standardMicro1",Vitamins.listVitaminSizes("hobbyServoHorn"))
	LengthParameter eyeCenter 		= new LengthParameter("Eye Center Distance",40,[100,50])
	List<CSG> make(){
		CSG horn = Vitamins.get("hobbyServoHorn",hornSizeParam.getStrValue())	
					.roty(180).rotz(180+45).movez(1.5)
		CSG servo = Vitamins.get("hobbyServo",servoSizeParam.getStrValue())
					.toZMax()
					//.union(horn)
		
		Transform panServoLocation = new Transform()
								.translate(-eyemechRadius.getMM()*3,
								0,
								0)
		Transform panBearingLocation =  new Transform()
								.translate(-eyemechRadius.getMM()*3,
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
		linkKeepaway=linkKeepaway.union(linkKeepaway.move(6,3,0)).hull()
		linkKeepaway=linkKeepaway.union(linkKeepaway.movez(5)).hull()
		servolinkBlank=servolinkBlank.intersect(servolinkBlank.getBoundingBox().toZMin().movez(servolinkPin.getMinZ()))	
						.difference(linkKeepaway.movey(-eyemechRadius.getMM()))
						.difference(linkKeepaway.rotz(180).movex(-eyemechRadius.getMM()))
						.union(servolinkPin)
		CSG servoHornLinkage=servolinkBlank
						.difference(horn)
						.difference(horn.movez(1.5))
		CSG beringLinkage = 	servolinkBlank		
		CSG aSlice = slaveCup.intersect(slaveCup.getBoundingBox().toXMax().movex(slaveCup.getMinX()+cupThick))
		CSG bar = aSlice.union(aSlice.movey(eyeCenter.getMM())).hull()
		CSG slaveLinkage = slaveCup.union(slaveCup.movey(eyeCenter.getMM()))
						.union(bar)

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
		CSG panLinkage = makeLinkage(cupPanSrv,cupPan)
		CSG tiltLinkage = makeLinkage(cupTiltSrv,cup)
		return [tiltServo,panServo,eye,eyeMount,
		tiltLinkage,linkPinTilt,
		linkPinPan,panLinkage,
		linkPinTiltBearing,linkPinPanBearing,
		slaveLinkagePan,slaveLinkageTilt]
	}
	CSG makeLinkage(CSG a, CSG b){
		CSG aSlice = a.intersect(a.getBoundingBox().toXMin().movex(a.getMaxX()-1))
		CSG bSlice = b.intersect(b.getBoundingBox().toXMax().movex(b.getMinX()+1))
		CSG bar =aSlice.union(bSlice).hull()
		return CSG.unionAll([a,b,bar])
	}
}

return new HeadMakerClass().make()