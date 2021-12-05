import PIL, sys, math, cv2, argparse
from PIL import Image

filename = None
outputname = None

width = 128
height = 36

frames = []


def parse():
    global filename
    global outputname
    file_out = open(outputname, "w")
    if filename[-4:] == ".mp4":
        convert_mp4(file_out)
    file_out.close()


def convert_mp4(file):
    global filename, width, height
    count = 0
    video = cv2.VideoCapture(filename)
    success, image = video.read()
    success = True
    while success:
        video.set(cv2.CAP_PROP_POS_MSEC, (count * keying_freq))
        success, image = video.read()
        if not success:
            break
        img = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        frame = Image.fromarray(img)
        frame = frame.resize((width, height))
        for i in range(frame.width):
            for j in range(frame.height):
                pos = i,j
                px = frame.getpixel(pos)
                r = int(px[0])
                g = int(px[1])
                b = int(px[2])
                file.write(f"{r: <3} {g: <3} {b: <3} \t")
            file.write("\n")
        file.write('End of frame no. ' + str(count) + '\n')
        print('Parsed frame no. ' + str(count))
        count +=1
    print("DONE!")

def main():
    global filename, outputname, width, height, keying_freq
    filename = sys.argv[1]
    outputname = sys.argv[2]
    width = int(sys.argv[3])
    height = int(sys.argv[4])
    keying_freq = int(sys.argv[5])
    parse()

main()