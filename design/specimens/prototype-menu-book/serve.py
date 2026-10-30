"""PROTOTIPO descartable: python design/specimens/prototype-menu-book/serve.py"""
from http.server import ThreadingHTTPServer, SimpleHTTPRequestHandler
from pathlib import Path
from functools import partial
import mimetypes

folder = Path(__file__).resolve().parents[1]
class Handler(SimpleHTTPRequestHandler):
    def do_GET(self):
        if self.path.startswith('/simple-assets/'):
            name = self.path.split('?', 1)[0].removeprefix('/simple-assets/')
            asset_folder = 'assets/exports/prototype-menu-simple-v3' if name.endswith('.webp') else 'assets/source/menu/simple-v3'
            root = (folder.parents[1] / asset_folder).resolve()
            target = (root / name).resolve()
            if not target.is_relative_to(root) or not target.is_file():
                self.send_error(404)
                return
            data = target.read_bytes()
            self.send_response(200)
            self.send_header('Content-Type', mimetypes.guess_type(target.name)[0] or 'application/octet-stream')
            self.end_headers()
            self.wfile.write(data)
            return
        if self.path.startswith('/art-assets/'):
            name = self.path.split('?', 1)[0].removeprefix('/art-assets/')
            asset_folder = 'assets/exports/prototype-menu-v2' if name.endswith('.webp') else 'assets/source/menu/atlas-v2'
            root = (folder.parents[1] / asset_folder).resolve()
            target = (root / name).resolve()
            if not target.is_relative_to(root) or not target.is_file():
                self.send_error(404)
                return
            data = target.read_bytes()
            self.send_response(200)
            self.send_header('Content-Type', mimetypes.guess_type(target.name)[0] or 'application/octet-stream')
            self.end_headers()
            self.wfile.write(data)
            return
        if self.path == '/tokens.json':
            data = (folder.parents[1] / 'design-tokens.json').read_bytes()
            self.send_response(200)
            self.send_header('Content-Type', 'application/json')
            self.end_headers()
            self.wfile.write(data)
        else:
            super().do_GET()

print('Libros 2D: http://127.0.0.1:8765/prototype-menu-book/simple-gallery.html', flush=True)
ThreadingHTTPServer(('127.0.0.1', 8765), partial(Handler, directory=str(folder))).serve_forever()
