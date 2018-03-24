//Your code here
if (args==null){
	args=[38]
	CSGDatabase.clear()
}
class EyeMakerClass{

	StringParameter boltSizeParam 			= new StringParameter("Bolt Size","8#32",Vitamins.listVitaminSizes("capScrew"))
	
	HashMap<String, Object>  boltMeasurments = Vitamins.getConfiguration( "capScrew",boltSizeParam.getStrValue())
	HashMap<String, Object>  nutMeasurments = Vitamins.getConfiguration( "nut",boltSizeParam.getStrValue())

	LengthParameter eyemechRadius		= new LengthParameter("Eye Mech Linkage",12,[20,5])
	LengthParameter boltDiam 		= new LengthParameter("Bolt Diameter",3.0,[8,2])
	LengthParameter nutDiam 		 	= new LengthParameter("Nut Diameter",5.42,[10,3])
	LengthParameter nutThick 		= new LengthParameter("Nut Thickness",2.4,[10,3])
	LengthParameter printerOffset		= new LengthParameter("printerOffset",0.5,[2,0.001])
	LengthParameter thickness 		= new LengthParameter(	"Material Thickness",
													5.1,
													[10,1])
	LengthParameter ballJointPinSize 		= new LengthParameter("Ball Joint Ball Radius",8,[50,4])
	double ballRadius = 4
	CSG getEyeLinkageCup(){
		double overallThickness = 1.28*ballRadius//  Z dimention
		//println boltMeasurments.toString() +" and "+nutMeasurments.toString()
		double boltDimeMeasurment = boltMeasurments.get("outerDiameter")
		double nutDimeMeasurment = nutMeasurments.get("width")
		double nutThickMeasurment = nutMeasurments.get("height")
		boltDiam.setMM(boltDimeMeasurment)
		nutDiam.setMM(nutDimeMeasurment)
		nutThick.setMM(nutThickMeasurment)
		double ballSize  = ballRadius+printerOffset.getMM()/2
		

		CSG cup = new Sphere((1.32*ballSize )-
						printerOffset.getMM()
		).toCSG()
		CSG pin = new Sphere(ballSize,30,15).toCSG()
		
		CSG ringBox =new Cube(	3*4,// X dimention
			3*4,// Y dimention
			overallThickness//  Z dimention
			).toCSG()// 
			.movex(3*4/3)
		CSG linkage =new Cube(	3*3,// X dimention
			ballRadius,// Y dimention
			overallThickness//  Z dimention
			).toCSG()// 
			.toXMin()
			.movex(3)
		cup = cup.intersect(ringBox)
				.union(linkage)
				.difference(pin)
				//.difference(Link)
		return cup.rotz(180).movez(eyemechRadius.getMM())
	}
	List<CSG>  getEye(double diameter){
	
		if(eyeCache.get(diameter)!=null){
			println "getting Eye cached"
			return eyeCache.get(diameter).clone()
		}
		ballJointPinSize.setMM(ballRadius)
		ArrayList<CSG> ballJointParts= (ArrayList<CSG>)ScriptingEngine.gitScriptRun(
	                                "https://github.com/madhephaestus/cablePullServo.git", // git location of the library
		                              "ballJointBall.groovy" , // file to load
		                              null// no parameters (see next tutorial)
	                        )
	     CSG ballJoint = ballJointParts.get(0)
		CSG ballJointKeepAway = ballJointParts.get(1)                   
		double boltDimeMeasurment = boltMeasurments.get("outerDiameter")
		double nutDimeMeasurment = nutMeasurments.get("width")
		double nutThickMeasurment = nutMeasurments.get("height")
		boltDiam.setMM(boltDimeMeasurment)
		double cupOffset = ballRadius/2
		
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
		CSG eye = new Sphere(diameter/2,30,15)// Spheres radius
					.toCSG()// convert to CSG to display
					.difference(new Cube(diameter).toCSG().toXMax().movex(-cupOffset))
					//.difference(new Cube(diameter).toCSG().toXMin().movex(diameter/2-6))// form the flat on the front of the eye
					.difference(ballJointKeepAway)
		//return eye
		
		CSG slot = new Sphere(1.6*ballRadius,30,7).toCSG()
		CSG pin = linkPin()
		
		slot = slot.movex(-cupOffset)
				.union(slot)
				.hull()
		slot=slot.difference(pin)
		for (int i=0;i<3;i++){
			
			eye=eye
			.difference(
				slot
				.movez(eyemechRadius.getMM())
				.rotx(90*i-90))
				
		}
		/*
		eye=eye.union( getEyeLinkageCup()
					.roty(180)
					.movez(eyemechRadius.getMM()+boltDiam.getMM()/2))
		*/			
		eyeCache.put(diameter,eye)
		//return eye.union(getEyeLinkageCup().movez(eyemechRadius.getMM()+boltDiam.getMM()/2))
		return [eye,ballJoint.rotz(180),ballJointKeepAway]
	}
	CSG linkPin(){
		CSG pinSupport = new RoundedCube(5,2.5,6)
						.cornerRadius(1)
						.toCSG()
						.toZMax()
						.movez(-5)
						.toXMin()
						.movex(-4.5)
						
		CSG pin = new Sphere(ballRadius,30,15).toCSG()
					.union(
					new Cylinder(	2.5,
								2.5,
								12,(int)15)
					.toCSG() 
					.toZMax()
					)
					.union(pinSupport)
	}
	HashMap<Double,CSG> eyeCache=new HashMap<>();
	List<CSG> make(double size){
		def parts=getEye(size)
		parts.addAll([getEyeLinkageCup(),linkPin().movez(eyemechRadius.getMM())])
		return parts
	}
}
return new EyeMakerClass().make(args.get(0))