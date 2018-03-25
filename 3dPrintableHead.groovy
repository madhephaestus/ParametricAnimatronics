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
		
		CSG servo = Vitamins.get("hobbyServo",servoSizeParam.getStrValue())
					.toZMax()
					.union(horn.roty(180).rotz(180+45).movez(1.5))
		CSG tiltServo = servo
					.movex(-eyemechRadius.getMM()*2)
					.movez(eyemechRadius.getMM())
					.movey(eyemechRadius.getMM())
		CSG panServo = servo
					//.roty(180)
					.movex(-eyemechRadius.getMM()*3)
					
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
		CSG cupPan = cup.rotx(-90)
		CSG cupTiltSrv = cup
					.rotz(180)
					.movex(-eyemechRadius.getMM()*2)
		CSG cupPanSrv	=cupTiltSrv
					.rotx(-90)
					.movex(-eyemechRadius.getMM())		
		double cupThick = cup.getTotalZ()
		CSG linkPin =  eyeParts.get(4)
						.movez(-eyemechRadius.getMM())
		linkPin=linkPin
				.intersect(linkPin.getBoundingBox().toZMin().movez(-cupThick/2))
		
		CSG linkPinTilt =linkPin
				.movex(-eyemechRadius.getMM()*2)
				.movez(eyemechRadius.getMM())
		CSG linkPinPan =linkPin
				.movex(-eyemechRadius.getMM()*3)
				.movey(-eyemechRadius.getMM())
		return [tiltServo,panServo,eye,eyeMount,cup,linkPinTilt,linkPinPan,cupPan,cupPanSrv,cupTiltSrv]
	}
}

return new HeadMakerClass().make()