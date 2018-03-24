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
					.union(horn.roty(180).rotz(180).movez(1.5))
		CSG tiltServo = servo
					.movex(-eyemechRadius.getMM()*2)
					.movez(eyemechRadius.getMM())
					.movey(eyemechRadius.getMM())
		CSG panServo = servo
					//.roty(180)
					.toXMax()
					.movex(tiltServo.getMinX()-2)
					
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
		CSG linkPin =  eyeParts.get(4)

		
		
		return [tiltServo,panServo,eye,eyeMount,cup]
	}
}

return new HeadMakerClass().make()