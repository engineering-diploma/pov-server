import os.path
import sys
import cv2
from multiprocessing import Pool, cpu_count
from PIL import Image

args = sys.argv

filename = args[1]
outputname = args[2]
width = int(args[3])
height = int(args[4])
keying_freq = int(args[5])
mode = 'SINGLE'

if len(args) == 7 and (args[6] == 'SINGLE' or args[6] == 'DOUBLE'):
    mode = args[6]

mp_count = cpu_count()

buffer = []
def set_buffer():
    if not os.path.isfile(filename):
        print("Wrong file provided!")
        return False
    video = cv2.VideoCapture(filename)
    success, image = video.read()
    step = 0
    while success:
        video.set(cv2.CAP_PROP_POS_MSEC, step * keying_freq)
        success, image = video.read()
        if not success:
            break
        buffer.append([step, image])
        step += 1
    return True


def save_to_file(buffer_out):
    with open(outputname, "w") as file:
        for idx, frame in buffer_out:
            if frame is None:
                continue
            for row in frame:
                for i in range(0, len(row), 3):
                    file.write(f"{row[i]: <3} {row[i + 1]: <3} {row[i + 2]: <3} \t")
                file.write("\n")
            file.write(f'End of frame no. {idx}\n')


def prepare_frame(frame):
    result = []
    if mode == "SINGLE":
        for i in range(frame.width):
            row = []
            for j in range(frame.height):
                pos = i, j
                px = frame.getpixel(pos)
                r = int(px[0])
                g = int(px[1])
                b = int(px[2])
                row.append(r)
                row.append(g)
                row.append(b)
            result.append(row)
    elif mode == "DOUBLE":
        half = int(3 * height / 2)
        first_strip = []
        second_strip = []
        for i in range(frame.width):
            row = []
            for j in range(frame.height):
                pos = i, j
                px = frame.getpixel(pos)
                r = int(px[0])
                g = int(px[1])
                b = int(px[2])
                row.append(r)
                row.append(g)
                row.append(b)
            if i % 2 == 0:
                first_strip.append(row)
            else:
                new_front = row[half:]
                new_back = row[:half]
                row = new_front + new_back
                second_strip.insert(0, row)
        result = first_strip + second_strip
    return result


def algorithm(buf):
    frame_idx, image = buf
    img = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    frame = Image.fromarray(img)
    frame = frame.resize((width, height))
    f = prepare_frame(frame)
    return frame_idx, f


if __name__ == "__main__":
    if set_buffer():
        pool = Pool(mp_count)
        results = pool.map(algorithm, buffer)
        save_to_file(results)
