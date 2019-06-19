import React from 'react';
// import { Link } from 'react-router-dom';

import API from '../../service/api';

export default class PreviewPelicula extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      imagen: '/img/placeholder.png',
      nombre: this.props.nombre,
      genero: this.props.genero,
      clasificacion: this.props.clasificacion,
      sinopsis: this.props.sinopsis,
      sala: this.props.sala,
    };
  }

  conseguirImagen() {
    API.get('')
      .then(response => this.cargarImagen(response))
      .catch(console.log('imagen no encontrada'));
  }

  cargarImagen() {

  }

  render() {
    return (
      <div className="card peli-card">
        <img className="card-img-top" src={this.state.imagen} alt={`imagen de ${this.state.titulo}`} />
        <div className="card-body">
          <h5 className="card-title">{this.state.titulo}</h5>
          <h5 className="card-title">Pelicula</h5>
          <p className="card-text">
            {`Genero: ${this.state.genero}`} <br />
            {`Nombre: ${this.state.nombre}`} <br />
            {`Clasificacion: ${this.state.clasificacion}`} <br />
            {`Sinopsis: ${this.state.sinopsis}`} <br />
            {`Numero de sala: ${this.state.sala}`} <br />
          </p>
          {/* <Link
            className="btn btn-primary"
            to={{ pathname: `/pelicula/${this.props.datos.codigo}`, state: { username: this.props.username } }}
          > Ver detalle
          </Link> */}
        </div>
      </div>);
  }
}
