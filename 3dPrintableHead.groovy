//Your code here
if (args==null){
	CSGDatabase.clear()
}
class HeadMakerClass{
	StringParameter servoSizeParam 			= new StringParameter("hobbyServo Default","DHV56mg_sub_Micro",Vitamins.listVitaminSizes("hobbyServo"))
	LengthParameter eyemechRadius		= new LengthParameter("Eye Mech Linkage",12,[20,5])
	StringParameter hornSizeParam 			= new StringParameter("hobbyServoHorn Default","standardMicro1",Vitamins.listVitaminSizes("hobbyServoHorn"))
	List<CSG> make(){
		CSG horn = Vitamins.get("hobbyServoHorn",hornSizeParam.getStrValue())	
					.roty(180).rotz(180+45).movez(1.5)
		CSG servo = Vitamins.get("hobbyServo",servoSizeParam.getStrValue())
					.toZMax()
					//.union(horn)
		Transform tiltServoLocation = new Transform()
								.translate(-eyemechRadius.getMM()*2,
								eyemechRadius.getMM(),
								eyemechRadius.getMM())
		Transform panServoLocation = new Transform()
								.translate(-eyemechRadius.getMM()*3,
								0,
								0)
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
		CSG eye = eyeParts.get(0)
	    	CSG eyeMount = eyeParts.get(1)
		CSG cup =  eyeParts.get(3)
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
					.movex(-eyemechRadius.getMM())
		servolinkPin=servolinkPin.union(	slaveLink
										.intersect(slaveLink.getBoundingBox().movez(1)))
				.movey(-eyemechRadius.getMM())
		CSG servolinkBlank= servolinkPin
				.union(horn)
				.hull()
				.toolOffset(2)
		CSG linkKeepaway  = new Sphere(7,30,7).toCSG()
		servolinkBlank=servolinkBlank.intersect(servolinkBlank.getBoundingBox().toZMin().movez(servolinkPin.getMinZ()))	
						.difference(linkKeepaway.movey(-eyemechRadius.getMM()))
						.difference(linkKeepaway.movex(-eyemechRadius.getMM()))
						.union(servolinkPin)
		CSG servoHornLinkage=servolinkBlank
						.difference(horn)
						.difference(horn.movez(1.5))
						
		CSG linkPinTilt =servoHornLinkage
				.transformed(tiltServoLocation)
		CSG linkPinPan =servoHornLinkage
				.transformed(panServoLocation)
		CSG panLinkage = makeLinkage(cupPanSrv,cupPan)
		CSG tiltLinkage = makeLinkage(cupTiltSrv,cup)
		return [tiltServo,panServo,eye,eyeMount,tiltLinkage,linkPinTilt,linkPinPan,panLinkage]
	}
	CSG makeLinkage(CSG a, CSG b){
		CSG aSlice = a.intersect(a.getBoundingBox().toXMin().movex(a.getMaxX()-1))
		CSG bSlice = b.intersect(b.getBoundingBox().toXMax().movex(b.getMinX()+1))
		CSG bar =aSlice.union(bSlice).hull()
		return CSG.unionAll([a,b,bar])
	}
}

return new HeadMakerClass().make()