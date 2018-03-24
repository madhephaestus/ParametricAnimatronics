//Your code here
if (args==null)
	args=[20]
class EyeMakerClass{
	ArrayList<CSG> ballJointParts= (ArrayList<CSG>)ScriptingEngine.gitScriptRun(
		                                "https://github.com/madhephaestus/cablePullServo.git", // git location of the library
			                              "ballJointBall.groovy" , // file to load
			                              null// no parameters (see next tutorial)
		                        )
	
	CSG ballJoint = ballJointParts.get(0)
	CSG ballJointKeepAway = ballJointParts.get(1)
	LengthParameter eyemechRadius		= new LengthParameter("Eye Mech Linkage",15,[20,5])
	
	CSG getEyeLinkageCup(){
		println boltMeasurments.toString() +" and "+nutMeasurments.toString()
		double boltDimeMeasurment = boltMeasurments.get("outerDiameter")
		double nutDimeMeasurment = nutMeasurments.get("width")
		double nutThickMeasurment = nutMeasurments.get("height")
		boltDiam.setMM(boltDimeMeasurment)
		nutDiam.setMM(nutDimeMeasurment)
		nutThick.setMM(nutThickMeasurment)
		double ballSize  = 5+printerOffset.getMM()/2
		
		CSG mechLinkageCore = new Cylinder(boltDiam.getMM(),
								boltDiam.getMM(),
								thickness.getMM(),
								(int)15).toCSG()
								.toXMin()
								.movex(ballSize)
								.movez(-thickness.getMM()/2)
		CSG Link = CSG.unionAll([mechLinkageCore,
							mechLinkageCore.movex(10)
		]).hull()
		Link=Link.union(Link.rotx(90))
		
		CSG cup = new Sphere((3*2.2 )-
						printerOffset.getMM()
		).toCSG()
		CSG pin = new Sphere(ballSize,30,15).toCSG()
		double overallThickness = 3.2*2//  Z dimention
		CSG ringBox =new Cube(	3*4,// X dimention
			3*4,// Y dimention
			overallThickness//  Z dimention
			).toCSG()// 
			.movex(3*4/3)
		CSG linkage =new Cube(	3*3,// X dimention
			boltDimeMeasurment*2.5,// Y dimention
			overallThickness//  Z dimention
			).toCSG()// 
			.toXMin()
			.movex(3)
		cup = cup.intersect(ringBox)
				.union(linkage)
				.difference(pin)
				.difference(Link)
		return cup.rotz(180)
	}
	CSG getEye(double diameter){
		
		if(eyeCache.get(diameter)!=null){
			println "getting Eye cached"
			return eyeCache.get(diameter).clone()
		}
		
		double cupOffset = 4
		
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
					.difference(new Cube(diameter).toCSG().toXMin().movex(diameter/2-6))
					.difference(ballJointKeepAway)
		//return eye
		
		CSG slot = new Sphere(8,30,7).toCSG()
		CSG pinSupport = new RoundedCube(5,2.5,6)
						.cornerRadius(1)
						.toCSG()
						.toZMax()
						.movez(-5)
						.toXMin()
						.movex(-4.5)
						
		CSG pin = new Sphere(5,30,15).toCSG()
					.union(
					new Cylinder(	2.5,
								2.5,
								12,(int)15)
					.toCSG() 
					.toZMax()
					)
					.union(pinSupport)
		
		slot = slot.movex(-cupOffset)
				.union(slot)
				.hull()
		slot=slot.difference(pin)
		for (int i=0;i<3;i++){
			
			eye=eye
			.difference(
				slot
				.movez(eyemechRadius.getMM()+boltDiam.getMM()/2)
				.rotx(90*i-90))
				
		}
		/*
		eye=eye.union( getEyeLinkageCup()
					.roty(180)
					.movez(eyemechRadius.getMM()+boltDiam.getMM()/2))
		*/			
		eyeCache.put(diameter,eye)
		//return eye.union(getEyeLinkageCup().movez(eyemechRadius.getMM()+boltDiam.getMM()/2))
		return eye
	}
	HashMap<Double,CSG> eyeCache=new HashMap<>();
	List<CSG> make(double size){
		return [getEye(size),getEyeLinkageCup()]
	}
}
return new EyeMakerClass().make(args.get(0))