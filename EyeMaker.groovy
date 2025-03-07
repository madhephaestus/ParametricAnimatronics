import com.neuronrobotics.bowlerstudio.vitamins.Vitamins

import eu.mihosoft.vrl.v3d.CSG
import eu.mihosoft.vrl.v3d.Cube
import eu.mihosoft.vrl.v3d.Cylinder
import eu.mihosoft.vrl.v3d.RoundedCube
import eu.mihosoft.vrl.v3d.Sphere
import eu.mihosoft.vrl.v3d.parametrics.CSGDatabase
import eu.mihosoft.vrl.v3d.parametrics.LengthParameter
import eu.mihosoft.vrl.v3d.parametrics.StringParameter

//Your code here
boolean devMode = false
if (args==null){
	args=[38]
	CSGDatabase.clear()
	devMode = true
	println "Development mode for eyes"
}
LengthParameter printerOffset		= new LengthParameter("printerOffset",0.2,[2,0.001])
printerOffset.setMM(0.2)
class EyeMakerClass{
	ArrayList<CSG> ballJointParts=null
	StringParameter boltSizeParam 			= new StringParameter("Bolt Size","M3",Vitamins.listVitaminSizes("capScrew"))
	
	HashMap<String, Object>  boltMeasurments = Vitamins.getConfiguration( "capScrew",boltSizeParam.getStrValue())
	HashMap<String, Object>  nutMeasurments = Vitamins.getConfiguration( "nut",boltSizeParam.getStrValue())

	LengthParameter eyemechRadius		= new LengthParameter("Eye Mech Linkage",12,[20,5])
	LengthParameter boltDiam 		= new LengthParameter("Bolt Diameter",3.0,[8,2])
	LengthParameter nutDiam 		 	= new LengthParameter("Nut Diameter",5.42,[10,3])
	LengthParameter nutThick 		= new LengthParameter("Nut Thickness",2.4,[10,3])
	LengthParameter printerOffset		= new LengthParameter("printerOffset",0.2,[2,0.001])
	LengthParameter thickness 		= new LengthParameter(	"Material Thickness",
													5.1,
													[10,1])
	LengthParameter ballJointPinSize 		= new LengthParameter("Ball Joint Ball Radius",8,[50,4])
	CSG ballJoint=null
	CSG ballJointKeepAway =null
	double ballRadius = 4.5
	double servoSweep = 60
	CSG supportPin =new Cylinder(	1.75,
								12)
					.toCSG() 
	CSG getEyeLinkageCup(){
		double overallThickness = 1.28*ballRadius//  Z dimention
		//println boltMeasurments.toString() +" and "+nutMeasurments.toString()
		double boltDimeMeasurment = boltMeasurments.get("outerDiameter")
		double nutDimeMeasurment = nutMeasurments.get("width")
		double nutThickMeasurment = nutMeasurments.get("height")
		boltDiam.setMM(boltDimeMeasurment)
		nutDiam.setMM(nutDimeMeasurment)
		nutThick.setMM(nutThickMeasurment)
		double ballSize  = ballRadius+printerOffset.getMM()*0.75
		

		CSG cup = new Sphere((1.32*ballSize )
		).toCSG()
		CSG pin = new Sphere(ballSize,30,15).toCSG()
		
		CSG ringBox =new Cube(	3*4,// X dimention
			3*4,// Y dimention
			overallThickness//  Z dimention
			).toCSG()// 
			.movex(3)
		CSG linkage =new Cube(	3*3,// X dimention
			ballRadius,// Y dimention
			overallThickness//  Z dimention
			).toCSG()// 
			.toXMin()
			.movex(3)
		cup = cup.intersect(ringBox)
				.union(linkage)
				.difference(pin)
				.difference(Extrude.revolve(supportPin.roty(90),
		(double)0, // rotation center radius, if 0 it is a circle, larger is a donut. Note it can be negative too
		(double)servoSweep,// degrees through wich it should sweep
		(int)10
		).collect{it.rotz(servoSweep/-2)}
		)
		return cup.rotz(180).movez(eyemechRadius.getMM())
	}
	List<CSG>  getEye(double diameter){
	
		if(eyeCache.get(diameter)!=null){
			println "getting Eye cached"
			return [eyeCache.get(diameter).clone(),ballJoint.rotz(180),ballJointKeepAway]
		}
		ballJointPinSize.setMM(ballRadius)
		ArrayList<CSG> ballJointParts= (ArrayList<CSG>)ScriptingEngine.gitScriptRun(
	                                "https://github.com/madhephaestus/cablePullServo.git", // git location of the library
		                              "ballJointBall.groovy" , // file to load
		                              null// no parameters (see next tutorial)
	                        )
	     ballJoint = ballJointParts.get(0)
		ballJointKeepAway = ballJointParts.get(1)                   
		double boltDimeMeasurment = boltMeasurments.get("outerDiameter")
		double nutDimeMeasurment = nutMeasurments.get("width")
		double nutThickMeasurment = nutMeasurments.get("height")
		boltDiam.setMM(boltDimeMeasurment)
		double cupOffset = ballRadius*0.75
		
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
		CSG backOfEyeCutter =new Cube(diameter).toCSG().toXMax().movex(-cupOffset)
		CSG eye = new Sphere(diameter/2,30,15)// Spheres radius
					.toCSG()// convert to CSG to display
					.difference(backOfEyeCutter)// back of the eye
					.difference(new Cube(diameter).toCSG().toXMin().movex(diameter/2-6))// form the flat on the front of the eye
					.difference(ballJointKeepAway)
		//return eye
		
		CSG slot = new Sphere(1.6*ballRadius+printerOffset.getMM()/2,30,7).toCSG()
		CSG pin = linkPin()
		
		slot = slot.movex(-cupOffset)
				.union(slot)
				.hull()
		slot=slot.difference(pin)
		for (int i=0;i<2;i++){
			
			eye=eye
			.difference(
				slot
				.rotx(-(90*i-90))
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
					.union(supportPin 
					.toZMax()
					)
					//.union(pinSupport)
		return pin
	}
	HashMap<Double,CSG> eyeCache=new HashMap<>();
	List<CSG> make(double size){
		def parts=getEye(size)
		parts.addAll([getEyeLinkageCup(),linkPin().movez(eyemechRadius.getMM())])
		return parts
	}
}
if(devMode)
	return new EyeMakerClass().make(args.get(0))
else
	return new EyeMakerClass()