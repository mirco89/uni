function [LNew, ANew, bNew, ind] = simplex(L, A, b, in)
    % TODO:
    % Vectorize all of that
    % keep track of interchanged index
    % return correct values if d >= 0
    % catch inf cycle
    arrayDim = size(A);
    ind = zeros(1,arrayDim(1)+arrayDim(2));
    if length(in) <= 1
        for i = 1:arrayDim(1)+arrayDim(2)
            ind(i) = i;
        end
    else
        ind = in;
    end
    simplexTable = A;
    simplexTable(:,arrayDim(2)+1) = b';
    if arrayDim(2)+1 ~= length(L)
        L = [L,0];
    end
    simplexTable(arrayDim(1)+1,:) = L;
    simplexTable
    [~, s] = min(simplexTable(arrayDim(1)+1,:));
    if max(simplexTable(1:arrayDim(1),s)) <= 0
        LNew = [];
        ANew = [];
        bNew = [];
        return
    end
    pos = simplexTable(1:arrayDim(1),s);
    pos(pos < 0) = 0;
    [~, r] = min(simplexTable(1:arrayDim(1),arrayDim(2)+1)./pos);
    disp(['s=',num2str(s)])
    disp(['r=',num2str(r)])
    tempTable = simplexTable;
    for i = 1:arrayDim(1)
        for j = 1:arrayDim(2)
            if i ~= r && j ~= s
                tempTable(i,j) = simplexTable(i,j) - simplexTable(i,s)*simplexTable(r,j)/simplexTable(r,s);
            end
            if i == r && j == s
                tempTable(i,j) = 1/simplexTable(r,s);
            end
            if i ~= r && j == s
                tempTable(i,j) = -simplexTable(i,s)/simplexTable(r,s);
            end
            if i == r && j ~= s
                tempTable(i,j) = simplexTable(r,j)/simplexTable(r,s);
            end
        end
    end
    for i = 1:arrayDim(1)
        if i == r
            tempTable(i,arrayDim(2)+1) = simplexTable(r,arrayDim(2)+1)/simplexTable(r,s);
        end
        if i ~= r
            tempTable(i,arrayDim(2)+1) = simplexTable(i,arrayDim(2)+1) - simplexTable(r,arrayDim(2)+1)*simplexTable(i,s)/simplexTable(r,s);
        end
    end
    for j = 1:arrayDim(2)
        if j == s
            tempTable(arrayDim(1)+1,j) = -simplexTable(arrayDim(1)+1,j)/simplexTable(r,s);
        end
        if j ~= s
            tempTable(arrayDim(1)+1,j) = simplexTable(arrayDim(1)+1,j) - simplexTable(arrayDim(1)+1,s)*simplexTable(r,j)/simplexTable(r,s);
        end
    end
    tempTable(arrayDim(1)+1,arrayDim(2)+1) = simplexTable(arrayDim(1)+1,arrayDim(2)+1) + simplexTable(arrayDim(1)+1,s)*simplexTable(r,arrayDim(2)+1)/simplexTable(r,s);
    simplexTable = tempTable
    LNew = simplexTable(arrayDim(1)+1,:);
    ANew = simplexTable(1:arrayDim(1),1:arrayDim(2));
    bNew = simplexTable(1:arrayDim(1),arrayDim(2)+1);
    tempIndex = ind(s);
    ind(s) = ind(arrayDim(2)+r);
    ind(arrayDim(2)+r) = tempIndex;
    for i = 1:arrayDim(1)+arrayDim(2)
        if ind(i) <= arrayDim(1)
            if i <= arrayDim(2)
                disp(['x',num2str(ind(i)),'=',num2str(simplexTable(arrayDim(1)+1,i))]);
            else
                disp(['x',num2str(ind(i)),'=',num2str(simplexTable(i-arrayDim(2),arrayDim(2)+1))]);
            end
        end
    end
end
