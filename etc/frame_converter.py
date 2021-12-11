import sys, cv2, os
from multiprocessing import Pool, cpu_count, Lock
from multiprocessing.pool import ThreadPool
from PIL import Image
from queue import Queue

filename = sys.argv[1]
outputname = sys.argv[2]
width = int(sys.argv[3])
height = int(sys.argv[4])
keying_freq = int(sys.argv[5])

mode = "SINGLE"
if len(sys.argv) == 7:
    mode = sys.argv[6]
mp_count = cpu_count()  # TODO insert cpu count as param(maybe)

buffer = []


def set_buffer():
    video = cv2.VideoCapture(filename)
    success, image = video.read()
    step = 0
    while success:
        video.set(cv2.CAP_PROP_POS_MSEC, step * keying_freq)
        success, image = video.read()
        buffer.append([step, image])
        step += 1


def save_to_file(buffer):
    with open(outputname, "w") as file:
        for idx, frame in buffer:
            if frame is None:
                continue
            for row in frame:
                for c in row:
                    file.write(f"{c} ")
                file.write("\n")
            file.write(f'End of frame no. {idx}\n')


def parsepixel(pixel):
    pass


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
            print(len(row))
            result.append(row)
    elif mode == "DOUBLE":
        pass
    return result


def algorithm(buf):
    frame_idx, image = buf
    if image is None:
        return
    img = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    frame = Image.fromarray(img)
    frame = frame.resize((width, height))
    f = prepare_frame(frame)
    return frame_idx, f


if __name__ == "__main__":
    set_buffer()
    pool = ThreadPool(mp_count)
    results = pool.map(algorithm, buffer)
    # results = [algorithm(x) for x in buffer]
    results.remove(None)
    save_to_file(results)
