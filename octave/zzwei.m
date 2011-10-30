function fn = zzwei(x1,y1,z1,b1,x2,y2,z2,b2,x3,y3,z3,b3)
	A = [x1,y1,z1,b1;x2,y2,z2,b2;x3,y3,z3,b3]
	if (A(1,1) != 0)
		A = [1,A(1,2)/A(1,1),A(1,3)/A(1,1),A(1,4)/A(1,1);A(2,1),A(2,2),A(2,3),A(2,4);A(3,1),A(3,2),A(3,3),A(3,4)];
	endif
	# A(2,1) = 0
	A = [A(1,1),A(1,2),A(1,3),A(1,4);A(2,1)-A(1,1)*A(2,1),A(2,2)-A(1,2)*A(2,1),A(2,3)-A(1,3)*A(2,1),A(2,4)-A(1,4)*A(2,1);A(3,1),A(3,2),A(3,3),A(3,4)];
	if (A(2,2) != 0)
		A = [A(1,1),A(1,2),A(1,3),A(1,4);A(2,1)/A(2,2),1,A(2,3)/A(2,2),A(2,4)/A(2,2);A(3,1),A(3,2),A(3,3),A(3,4)];
	endif
	# A(1,2) = 0
	A = [A(1,1)-A(1,2)*A(2,1),A(1,2)-A(1,2)*A(2,2),A(1,3)-A(1,2)*A(2,3),A(1,4)-A(1,2)*A(2,4);A(2,1),A(2,2),A(2,3),A(2,4);A(3,1),A(3,2),A(3,3),A(3,4)];
	# A(3,1) = 0
	A = [A(1,1),A(1,2),A(1,3),A(1,4);A(2,1),A(2,2),A(2,3),A(2,4);A(3,1)-A(1,1)*A(3,1),A(3,2)-A(1,2)*A(3,1),A(3,3)-A(1,3)*A(3,1),A(3,4)-A(1,4)*A(3,1)];
	# A(3,2) = 0
	A = [A(1,1),A(1,2),A(1,3),A(1,4);A(2,1),A(2,2),A(2,3),A(2,4);A(3,1)-A(2,1)*A(3,2),A(3,2)-A(2,2)*A(3,2),A(3,3)-A(2,3)*A(3,2),A(3,4)-A(2,4)*A(3,2)];
	if (A(3,3) != 0)
		A = [A(1,1),A(1,2),A(1,3),A(1,4);A(2,1),A(2,2),A(2,3),A(2,4);A(3,1)/A(3,3),A(3,2)/A(3,3),1,A(3,4)/A(3,3)];
	endif
	# A(1,3) = 0
	A = [A(1,1)-A(3,1)*A(1,3),A(1,2)-A(3,2)*A(1,3),A(1,3)-A(1,3)*A(3,3),A(1,4)-A(1,3)*A(3,4);A(2,1),A(2,2),A(2,3),A(2,4);A(3,1),A(3,2),A(3,3),A(3,4)];
	# A(2,3) = 0
	A = [A(1,1),A(1,2),A(1,3),A(1,4);A(2,1)-A(3,1)*A(2,3),A(2,2)-A(3,2)*A(2,3),A(2,3)-A(2,3)*A(3,3),A(2,4)-A(2,3)*A(3,4);A(3,1),A(3,2),A(3,3),A(3,4)];
	
	A = [A(1,1),A(1,2),A(1,3),A(1,4);A(2,1),A(2,2),A(2,3),A(2,4);A(3,1),A(3,2),A(3,3),A(3,4)]
	fn = [A(1,4),A(2,4),A(3,4)];
endfunction
