import pytest

from api.main import create_app

@pytest.fixture
def app():
    return create_app()
