<?php
	//for results date 1
	$sqlD1 = "SELECT date, lat, lng, vathos, megethos, type, typeSize FROM seismos
		WHERE date>=$fromdate1 AND date<=$todate1 AND 
		lat>=$flat[$j] AND lat<=$tlat[$j] AND 
		lng>=$flng[$i] AND lng<=$tlng[$i] AND 
		vathos>=$fromdpth AND vathos<=$todpth AND 
		megethos>=$frommagn AND megethos<=$tomagn
		ORDER BY $order $orderby";
	$time_start1 = microtime(true);
	$resultD1 = mysql_query($sqlD1);
	$time_end1 = microtime(true);
	$time1 = $time_end1 - $time_start1;
	$time1 = number_format($time1,4, ',', '');
	//for xml file
	$_SESSION['sqlD1'.$k1]=$sqlD1; 
	
	//for graph
	$sqlgrD1 = "SELECT date, megethos FROM seismos
		WHERE date>=$fromdate1 AND date<=$todate1 AND 
		lat>=$flat[$j] AND lat<=$tlat[$j] AND 
		lng>=$flng[$i] AND lng<=$tlng[$i] AND 
		vathos>=$fromdpth AND vathos<=$todpth AND 
		megethos>=$frommagn AND megethos<=$tomagn
		ORDER BY date";	
	$_SESSION['sqlgraphD1'.$k1]=$sqlgrD1;
	
	//for graphMagn
	//for compare with date 2
	$sqlgrmgnD1 = "SELECT megethos FROM seismos
		WHERE date>=$fromdate1 AND date<=$todate1 AND 
		lat>=$flat[$j] AND lat<=$tlat[$j] AND 
		lng>=$flng[$i] AND lng<=$tlng[$i] AND 
		vathos>=$fromdpth AND vathos<=$todpth AND 
		megethos>=$frommagn AND megethos<=$tomagn
	ORDER BY megethos";	
	$_SESSION['sqlgraphmagnD1'.$k1]=$sqlgrmgnD1;
	
	//for graphMagnPerYear
	$sqlgrmgnyearD1 = "SELECT date FROM seismos
		WHERE date>=$fromdate1 AND date<=$todate1 AND 
		lat>=$flat[$j] AND lat<=$tlat[$j] AND 
		lng>=$flng[$i] AND lng<=$tlng[$i] AND 
		vathos>=$fromdpth AND vathos<=$todpth AND 
		megethos>=$frommagn AND megethos<=$tomagn
		ORDER BY date";	
	$_SESSION['sqlgrmgnyearD1'.$k1]=$sqlgrmgnyearD1;

	//calculation of b
	$sqlb1 = "SELECT year, month, megethos FROM seismos
		WHERE date>=$fromdate1 AND date<=$todate1 AND 
		lat>=$flat[$j] AND lat<=$tlat[$j] AND 
		lng>=$flng[$i] AND lng<=$tlng[$i] AND 
		vathos>=$fromdpth AND vathos<=$todpth AND 
		megethos>=$frommagn AND megethos<=$tomagn
		ORDER BY year, month, megethos";	
	$_SESSION['sqlb1'.$k1]=$sqlb1;
	$sqlbyear1 = "SELECT year, month, megethos COUNT(*) AS totalearthquakes,
		MAX(megethos) AS maxMagn, MIN(megethos) AS minMagn,
		( MAX( megethos ) - MIN( megethos ) ) / 0.2 AS steps FROM seismos
		WHERE date>=$fromdate1 AND date<=$todate1 AND 
		lat>=$flat[$j] AND lat<=$tlat[$j] AND 
		lng>=$flng[$i] AND lng<=$tlng[$i] AND 
		vathos>=$fromdpth AND vathos<=$todpth AND 
		megethos>=$frommagn AND megethos<=$tomagn
		GROUP BY year, month
		ORDER BY year, month";
	//	echo $sqlb1."<br>";
	//	echo $sqlbyear1;
	$_SESSION['sqlbyear1'.$k1]=$sqlbyear1;

	//counter per grid
	$total_recordsD1=mysql_num_rows($resultD1);
?>