#!C:/Python27/python.exe -u
# -*- coding: ascii -*-
import numpy
import re
import string
import helperMK

fedTrainPath = "D:\\uni\\snlp\\FederalistTraining.txt"
fedTestPath = "D:\\uni\\snlp\\FederalistTestAll.txt"
outputPath = "D:\\uni\\snlp\\output\\"
names = ["Hamilton Train:", "Madison Train:", "Jay Train:"]
punctMarks = ["?","!",".",";",":",","]
alphabet = [character for character in string.lowercase[:26]]
fPersPron = ["i","me","my","mine","we","us","our","ours"]

print "Path to Train Set", fedTrainPath
print "Path to Test Set", fedTestPath
print "Path for Output", outputPath
print

# [[hamiltonDocs][madisonDocs][JayDocs]]
train = helperMK.authorArray(helperMK.readFile(fedTrainPath))
test = helperMK.testArray(helperMK.readFile(fedTestPath))

prior = []
featureMean = [[] for x in range(3)]
featureStd = [[] for x in range(3)]
for author in range(len(names)):
    # TRAINING
    print names[author]
    numToken = []
    numTypes = []
    lexDiv = []
    punct = [[] for x in range(6)]
    psum = []
    pron = []
    characters = [[] for x in range(26)]
    charsum = []
    wordLen = []
    prior.append(len(train[author]))
    for currDoc in range(len(train[author])):
        text = helperMK.extractText(train[author][currDoc])
        numToken.append(helperMK.getNumberOfToken(text))
        numTypes.append(helperMK.getNumberOfWordTypes(text))
        for k, letter in enumerate([text.count(x) for x in alphabet]):
            characters[k].append(letter)
        charsum.append(sum([text.count(letter) for letter in alphabet]))
        for k, punctSign in enumerate([text.count(x) for x in punctMarks]):
            punct[k].append(punctSign)
        psum.append(sum([text.count(punctSign) for punctSign in punctMarks]))
        pron.append(sum([text.count(pronoun) for pronoun in fPersPron])/numToken[-1])
        wordLen.append(charsum[-1]/numToken[-1])
        lexDiv.append(numToken[-1]/numTypes[-1])
    meanChar = []
    stdChar = []
    for letter in range(len(characters)):
        for charDoc in range(len(characters[letter])):
            characters[letter][charDoc] /= float(charsum[charDoc])
        meanChar.append(numpy.mean(characters[letter]))
        stdChar.append(numpy.std(characters[letter]))
    meanPunct = []
    stdPunct = []
    for punctSign in range(len(punct)):
        for punctDoc in range(len(punct[punctSign])):
            punct[punctSign][punctDoc] /= float(psum[punctDoc])
        meanPunct.append(numpy.mean(punct[punctSign]))
        stdPunct.append(numpy.std(punct[punctSign]))
    featureMean[author] = [numpy.mean(lexDiv), numpy.mean(pron), numpy.mean(wordLen)]
    featureMean[author].extend(meanPunct)
    featureMean[author].extend(meanChar)
    featureStd[author] = [numpy.std(lexDiv), numpy.std(pron), numpy.std(wordLen)]
    featureStd[author].extend(stdPunct)
    featureStd[author].extend(stdChar)
    print "mean", featureMean[author]
    print "std", featureStd[author]
    print "prior", prior[author]

for doc in range(len(test)):
    # TESTING
    text = helperMK.extractText(test[doc])
    noOfToken = helperMK.getNumberOfToken(text)
    lDiv = noOfToken/helperMK.getNumberOfWordTypes(text)
    prons = sum([text.count(x) for x in fPersPron])/noOfToken
    punct = [[] for x in range(6)]
    for k, punctSign in enumerate([text.count(x) for x in punctMarks]):
        punct[k] = punctSign
    psum = sum(punct)
    for punctSign in range(len(punct)):
        punct[punctSign] /= float(psum)
    characters = [[] for x in range(26)]
    for k, letter in enumerate([text.count(x) for x in alphabet]):
        characters[k] = letter
    charsum = sum(characters)
    for letter in range(len(characters)):
        characters[letter] /= float(charsum)
    wLen = charsum/noOfToken
    print
    pLD = []
    dLD = []
    for author in range(3):
        pLD.append(helperMK.evalProb(featureMean[author][0], featureStd[author][0], lDiv))
        dLD.append(abs(featureMean[author][0] - lDiv))
    pPron = []
    dPron = []
    for author in range(3):
        pPron.append(helperMK.evalProb(featureMean[author][1], featureStd[author][1], prons))
        dPron.append(abs(featureMean[author][1] - prons))
    pWL = []
    dWL = []
    for author in range(3):
        pWL.append(helperMK.evalProb(featureMean[author][2], featureStd[author][2], wLen))
        dWL.append(abs(featureMean[author][2] - wLen))
    pPunct = []
    dPunct = []
    for author in range(3):
        product = 1
        su = 0
        for punctSign in range(3,9):
            product *= helperMK.evalProb(featureMean[author][punctSign], featureStd[author][punctSign], punct[punctSign-3])
            su += abs(featureMean[author][punctSign] - punct[punctSign-3])
        pPunct.append(product)
        dPunct.append(su)
    pChar = []
    dChar = []
    for author in range(3):
        product = 1
        su = 0
        for letter in range(9,35):
            product *= helperMK.evalProb(featureMean[author][letter], featureStd[author][letter], characters[letter-9])
            su += abs(featureMean[author][letter] - characters[letter-9])
        pChar.append(product)
        dChar.append(su)
    pPrior = []
    for author in range(3):
        pPrior.append(prior[author]/float(sum(prior)))
    probabilities = []
    difference = []
    for author in range(3):
        probabilities.append(pLD[author] * pPron[author] * pWL[author] * pPunct[author] * pChar[author] * pPrior[author])
        difference.append(dLD[author] + dPron[author] + dWL[author] + dPunct[author] + dChar[author])
    print "Document Number", re.search("(?<=docno>).*?(?=</docno>)", test[doc]).group()
    print "H", int(1000*probabilities[0]/sum(probabilities))/10.0, "%"
    print "M", int(1000*probabilities[1]/sum(probabilities))/10.0, "%"
    print "J", int(1000*probabilities[2]/sum(probabilities))/10.0, "%"
    print
    print "H", difference[0]
    print "M", difference[1]
    print "J", difference[2]
    
