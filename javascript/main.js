import { Component, VirtualComponent, h } from '/component.js'

const SayHello = ({ test }) => {
  if (test) {
    return <coucou-coucou country-code="en" />
  } else {
    return null
  }
}

class Test extends VirtualComponent {
  static get observedAttributes() {
    return ['country-code', 'test']
  }

  render() {
    return (
      <div>
        Hello
        {this.countryCode}
        <SayHello test={this.test} />
        {this.test ? <coucou-coucou country-code="en" /> : null}
      </div>
    )
  }
}

export default Test
