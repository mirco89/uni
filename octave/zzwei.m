function fn = zzwei(x1,y1,z1,b1,x2,y2,z2,b2,x3,y3,z3,b3)
	A = [x1,y1,z1,b1;x2,y2,z2,b2;x3,y3,z3,b3]
	unique = 1;
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

	fprintf('----------------------\n')

	# not unique
	if(A(1,1) == 0)
		fprintf('  %.2fy= %f %+fx \n', A(2,2), A(2,4), -A(2,1))
		fprintf('  %.2fz= %f %+fx \n\n', A(3,3), A(3,4), -A(3,1))
		unique = 0;
	endif
	if(A(2,2) == 0)
		fprintf('  %.2fx= %.2f %+.2fy \n', A(1,1), A(1,4), -A(1,2))
		fprintf('  %.2fz= %.2f %+.2fy \n\n', A(3,3), A(3,4), -A(3,2))
		unique = 0;
	endif
	if(A(3,3) == 0)
		fprintf('  %.2fx= %.2f %+.2fz \n', A(1,1), A(1,4), -A(1,3))
		fprintf('  %.2fy= %.2f %+.2fz \n\n', A(2,2), A(2,4), -A(2,3))
		unique = 0;
	endif

	# zeroes
	if ((A(1,1:3) == 0 && A(1,4) != 0) || (A(2,1:3) == 0 && A(2,4) != 0) || (A(3,1:3) == 0 && A(3,4) != 0))
		fprintf('This equation is not solvable! %f \n\n', A(1,4))
		unique = 0;
	endif

	if (unique == 1)
		fn = [A(1,4),A(2,4),A(3,4)];
	endif
endfunction
