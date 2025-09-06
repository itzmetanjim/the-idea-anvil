import Image

im = Image.open('tweeter.png')
layer = Image.new('RGB', im.size, 'red')
output = Image.blend(im, layer, 0.5)
output.save('output.png', 'PNG')