function camelize(text) {
  return text.replace(/^([A-Z])|[\s-_]+(\w)/g, function(match, p1, p2, offset) {
    if (p2) return p2.toUpperCase()
    return p1.toLowerCase()
  })
}

function h(name, props, ...children) {
  if (typeof name === 'function') {
    return name({ ...props, children })
  }
  return { name, props: props || {}, children }
}

function removeChildren(node) {
  const children = [...node.children]
  children.forEach(child => node.removeChild(child))
}

function addAttributes(node, attributes) {
  const addAttr = ([attribute, value]) => node.setAttribute(attribute, value)
  Object.entries(attributes).forEach(addAttr)
}

function addChildren(node, children) {
  children.map(paintChildren).forEach(child => node.appendChild(child))
}

function paintChildren(node) {
  if (!node || typeof node === 'string') {
    return document.createTextNode(node || '')
  } else {
    const { name, props, children } = node
    const paint = document.createElement(name)
    addAttributes(paint, props)
    addChildren(paint, children)
    return paint
  }
}

function setAttributeInNode(attribute) {
  return function(value) {
    if (value) {
      if (typeof value === 'boolean') {
        this.setAttribute(attribute, '')
      } else {
        this.setAttribute(attribute, value)
      }
    } else {
      this.removeAttribute(attribute)
    }
  }
}

function getAttributeInNode(underscoredName) {
  return function() {
    return this[underscoredName]
  }
}

function attributeChangedCallback(name, oldValue, newValue) {
  const underscoredName = `_${camelize(name)}`
  this[underscoredName] = newValue
}

function addGettersAndSetters(node) {
  const { observedAttributes } = node.constructor
  if (observedAttributes) {
    observedAttributes.forEach(attribute => {
      const attributeName = camelize(attribute)
      const underscoredName = `_${attributeName}`
      const options = {
        get: getAttributeInNode(underscoredName),
        set: setAttributeInNode(attribute),
        enumerable: true,
      }
      Object.defineProperty(node, attributeName, options)
      Object.defineProperty(node, underscoredName, {
        value: null,
        enumerable: true,
        writable: true,
      })
    })
  }
}

class Base extends HTMLElement {
  constructor() {
    super()
    addGettersAndSetters(this)
  }
}

class Component extends Base {
  attributeChangedCallback(name, oldValue, newValue) {
    attributeChangedCallback.apply(this, [name, oldValue, newValue])
    this.render()
  }

  connectedCallback() {
    if (this.connected) {
      this.connected()
    } else if (
      !this.constructor.observedAttributes ||
      this.constructor.observedAttributes.length === 0
    ) {
      this.render()
    }
  }
}

class VirtualComponent extends Base {
  attributeChangedCallback(name, oldValue, newValue) {
    attributeChangedCallback.apply(this, [name, oldValue, newValue])
    this.repaint()
  }

  connectedCallback() {
    if (this.connected) {
      this.connected()
    } else if (
      !this.constructor.observedAttributes ||
      this.constructor.observedAttributes.length === 0
    ) {
      this.repaint()
    }
  }

  repaint() {
    const result = this.render()
    removeChildren(this)
    const node = paintChildren(result)
    this.appendChild(node)
  }
}

export { Component, VirtualComponent, h }
