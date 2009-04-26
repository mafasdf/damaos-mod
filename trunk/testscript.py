
import string, os

for x in range(0, 4):
	for y in range(0, 4):
		for z in range(0, 3):
			os.system('java -classpath \"bin;lib/geotools_repast.jar;lib/violinstrings-1.0.2.jar;lib/swtgraphics2d.jar;lib/asm.jar;lib/commons-logging.jar;lib/OpenForecast-0.4.0.jar;lib/jgap.jar;lib/jep-2.24.jar;lib/jfreechart-1.0.6-experimental.jar;lib/jfreechart-1.0.6-swt.jar;lib/servlet.jar;lib/plot.jar;lib/junit.jar;lib/swing-layout-1.0.jar;lib/commons-collections.jar;lib/JTS.jar;lib/beanbowl.jar;lib/gnujaxp.jar;lib/jcommon-1.0.10.jar;lib/jh.jar;lib/log4j-1.2.8.jar;lib/trove.jar;lib/jakarta-poi.jar;lib/jode-1.1.2-pre1.jar;lib/colt.jar;lib/ProActive.jar;lib/ibis.jar;lib/jfreechart-1.0.6.jar;lib/itext-2.0.2.jar;lib/jmf.jar;lib/openmap.jar;lib/joone.jar;lib/repast.jar\" finalProject.Console test %(x)d %(y)d %(z)d &' % {'x':x, 'y':y, 'z':z}) 
