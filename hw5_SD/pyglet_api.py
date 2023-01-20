import pyglet

from api import DrawingApi

pyglet_w = 1024
pyglet_h = 1024

window = pyglet.window.Window(pyglet_w, pyglet_h, "title")
batch = pyglet.graphics.Batch()

circles = []
lines = []
labels = []


@window.event
def on_draw():
    window.clear()
    batch.draw()


class PygletDrawingApi(DrawingApi):
    def __init__(self):
        self.w = self.get_drawing_area_width()
        self.h = self.get_drawing_area_height()

    def draw(self):
        pyglet.app.run()

    def get_drawing_area_width(self) -> int:
        return pyglet_w

    def get_drawing_area_height(self) -> int:
        return pyglet_h

    def draw_circle(self, radius: float, x: float, y: float, i: int):
        circle = pyglet.shapes.Circle(x, y, radius, color=self.white, batch=batch)
        circles.append(circle)
        label = pyglet.text.Label(
            text=f'{i}',
            font_name='Times New Roman',
            font_size=36,
            x=x,
            y=y,
            anchor_x='center',
            anchor_y='center',
            batch=batch,
            color=(0, 0, 0, 255)
        )
        labels.append(label)

    def draw_line(self, x1: float, y1: float, x2: float, y2: float):
        line = pyglet.shapes.Line(x1, y1, x2, y2, color=self.white, batch=batch)
        lines.append(line)
